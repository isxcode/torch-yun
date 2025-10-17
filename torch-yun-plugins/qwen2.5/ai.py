from fastapi import FastAPI
from pydantic import BaseModel, Field
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    AutoConfig,
    StoppingCriteria,
    StoppingCriteriaList
)
import torch
import os
from typing import Union, List

app = FastAPI()

# 模型加载（使用你本地路径）
model_path = os.getenv("MODEL_PATH")
config = AutoConfig.from_pretrained(f"{model_path}/config.json")
tokenizer = AutoTokenizer.from_pretrained(model_path)
model = AutoModelForCausalLM.from_pretrained(
    model_path,
    config=config,
    torch_dtype=torch.float16 if torch.cuda.is_available() else torch.float32,
    device_map="auto"
)

# 自定义 stopping criteria：当生成中包含指定字符串时停止
class StopOnString(StoppingCriteria):
    def __init__(self, stop_strings, tokenizer):
        self.stop_strings = stop_strings
        self.tokenizer = tokenizer

    def __call__(self, input_ids: torch.LongTensor, scores: torch.FloatTensor, **kwargs) -> bool:
        # 解码当前生成的文本
        decoded_text = self.tokenizer.decode(input_ids[0], skip_special_tokens=True)
        # 判断是否包含要截断的关键词
        for stop_str in self.stop_strings:
            if stop_str in decoded_text:
                return True
        return False

# 输入结构
class Message(BaseModel):
    role: str  # "system" | "user" | "assistant"
    content: str

class ChatRequest(BaseModel):
    prompt: Union[str, None]  = Field("我是Qwen2", description="可选，全局 system 提示，会自动拼接到 messages 第一条")
    messages: list[Message]

    # 生成控制参数
    maxTokens: int = Field(512, description="限制生成文本的最大长度")
    seed: Union[int, None] = Field(42, description="随机数种子，控制生成可重复性")
    topK: int = Field(50, description="限制候选词汇数量")
    topP: float = Field(0.9, description="限制候选词累计概率范围")
    temperature: float = Field(0.8, description="调整概率分布平滑程度")
    repetitionPenalty: float = Field(1.2, description="惩罚重复内容")
    enableSearch: bool = Field(False, description="启用搜索功能（占位参数）")

@app.get("/health")
async def health_check():
    return {"status": "healthy"}

@app.post("/chat")
async def chat_endpoint(request: ChatRequest):
    try:
        # 直接用用户传入的 messages 生成 prompt
#         messages = []
#         if request.prompt:
#             messages.append({"role": "system", "content": request.prompt})
#         messages.extend([m.dict() for m in request.messages])

        text = tokenizer.apply_chat_template(
            [m.dict() for m in request.messages],
            tokenize=False,
            add_generation_prompt=True
        )
        model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

        generation_config = {
            "max_new_tokens": request.maxTokens,
            "temperature": request.temperature,
            "top_k": request.topK,
            "top_p": request.topP,
            "repetition_penalty": request.repetitionPenalty
        }

        # 设置 stopping criteria
        stop_strings = ["ContentLoaded"]
        stopping_criteria = StoppingCriteriaList([StopOnString(stop_strings, tokenizer)])

        # 推理
        with torch.no_grad():
            generated_ids = model.generate(
                **model_inputs,
                **generation_config,
                stopping_criteria=stopping_criteria
            )

        # 去掉输入部分
        generated_ids = [
            output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
        ]

        response = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]
        return {"response": response}

    except Exception as e:
        return {"error": str(e)}