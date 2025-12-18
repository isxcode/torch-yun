from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    AutoConfig,
    StoppingCriteria,
    StoppingCriteriaList,
)
import torch
import os
from typing import Union, List, Optional, Sequence

app = FastAPI(title="Universal Hugging Face CausalLM API")

# ==================== 配置 ====================
# 通过环境变量配置模型路径或 HF repo id
MODEL_ID_OR_PATH = os.getenv("MODEL_ID", "Qwen/Qwen2-7B-Instruct")  # 示例：本地路径或 "meta-llama/Llama-3.1-8B-Instruct"
TORCH_DTYPE = torch.float16 if torch.cuda.is_available() else torch.float32
DEVICE_MAP = "auto"  # "auto", "balanced", "sequential" 或具体 device
LOW_CPU_MEM_USAGE = True
TRUST_REMOTE_CODE = bool(os.getenv("TRUST_REMOTE_CODE", "0")) == True

# ==================== 模型加载 ====================
print(f"Loading model from: {MODEL_ID_OR_PATH}")
config = AutoConfig.from_pretrained(MODEL_ID_OR_PATH, trust_remote_code=TRUST_REMOTE_CODE)
tokenizer = AutoTokenizer.from_pretrained(MODEL_ID_OR_PATH, trust_remote_code=TRUST_REMOTE_CODE)

# 许多模型需要添加 pad_token 或 eos_token 作为 pad_token
if tokenizer.pad_token is None:
    tokenizer.pad_token = tokenizer.eos_token

model = AutoModelForCausalLM.from_pretrained(
    MODEL_ID_OR_PATH,
    config=config,
    torch_dtype=TORCH_DTYPE,
    device_map=DEVICE_MAP,
    low_cpu_mem_usage=LOW_CPU_MEM_USAGE,
    trust_remote_code=TRUST_REMOTE_CODE,
)

print("Model loaded successfully.")


# ==================== 自定义 Stopping Criteria ====================
class StopOnStrings(StoppingCriteria):
    """当生成的文本中出现任意一个 stop string 时停止"""
    def __init__(self, stop_strings: List[str], tokenizer: AutoTokenizer):
        self.stop_strings = stop_strings
        self.tokenizer = tokenizer

    def __call__(self, input_ids: torch.LongTensor, scores: torch.FloatTensor, **kwargs) -> bool:
        decoded = self.tokenizer.decode(input_ids[0], skip_special_tokens=True)
        return any(stop_str in decoded for stop_str in self.stop_strings)


# ==================== 请求模型 ====================
class Message(BaseModel):
    role: str = Field(..., description="system | user | assistant")
    content: str

class ChatCompletionRequest(BaseModel):
    # 支持 OpenAI 风格的 messages
    messages: List[Message]

    # 可选 system prompt（如果 messages 中没有 system，可单独传）
    system: Optional[str] = None

    # 生成参数
    max_tokens: int = Field(512, alias="maxTokens", ge=1)
    temperature: float = Field(0.8, ge=0.0, le=2.0)
    top_p: float = Field(0.9, alias="topP", ge=0.0, le=1.0)
    top_k: int = Field(50, alias="topK", ge=1)
    repetition_penalty: float = Field(1.1, ge=1.0)
    seed: Optional[int] = None
    stop: Optional[List[str]] = None  # 停止字符串列表

    # 兼容旧字段（向后兼容）
    prompt: Optional[str] = Field(None, deprecated=True)  # 旧的单字符串 prompt


# ==================== 路由 ====================
@app.get("/health")
async def health_check():
    return {"status": "healthy", "model": MODEL_ID_OR_PATH}


@app.get("/model_info")
async def model_info():
    return {
        "model_id": MODEL_ID_OR_PATH,
        "tokenizer_chat_template": bool(getattr(tokenizer, "chat_template", None)),
        "device": next(model.parameters()).device.type if model.parameters() else "unknown",
    }


@app.post("/chat")
@app.post("/v1/chat/completions")  # 兼容 OpenAI 客户端
async def chat_completion(request: ChatCompletionRequest):
    try:
        # 1. 构建 messages
        final_messages = []
        if request.system:
            final_messages.append({"role": "system", "content": request.system})
        final_messages.extend([m.dict() for m in request.messages])

        # 2. 如果 tokenizer 有 chat_template，使用它（推荐方式，兼容 Qwen、Llama3、Phi3 等）
        if getattr(tokenizer, "chat_template", None) is not None:
            text = tokenizer.apply_chat_template(
                final_messages,
                tokenize=False,
                add_generation_prompt=True,
            )
        else:
            # fallback：手动拼接（适用于没有 chat_template 的老模型）
            text = "\n".join(
                f"<|{m['role']}|>{m['content']}<|end|>" for m in final_messages
            ) + "<|assistant|>"

        # 3. Tokenize
        model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

        # 4. 生成配置
        generaton_kwargs = {
            "max_new_tokens": request.max_tokens,
            "temperature": request.temperature if request.temperature > 0 else None,
            "top_p": request.top_p,
            "top_k": request.top_k,
            "repetition_penalty": request.repetition_penalty,
            "do_sample": request.temperature > 0,
            "pad_token_id": tokenizer.pad_token_id,
            "eos_token_id": tokenizer.eos_token_id,
        }

        # 5. Stopping criteria
        stopping_criteria = None
        if request.stop:
            stopping_criteria = StoppingCriteriaList([StopOnStrings(request.stop, tokenizer)])

        # 6. 推理
        with torch.no_grad():
            generated_ids = model.generate(
                **model_inputs,
                **generaton_kwargs,
                stopping_criteria=stopping_criteria,
            )

        # 7. 提取新生成的 token
        new_tokens = generated_ids[:, model_inputs.input_ids.shape[-1]:]
        response_text = tokenizer.batch_decode(new_tokens, skip_special_tokens=True)[0]

        return {"response": response_text.strip()}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))