from fastapi import FastAPI
from pydantic import BaseModel
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    AutoConfig,
    StoppingCriteria,
    StoppingCriteriaList
)
import torch
import os

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
    messages: list[Message]  # 全部对话历史（包含 system / user / assistant）

@app.post("/chat")
async def chat_endpoint(request: ChatRequest):
    try:
        # 直接用用户传入的 messages 生成 prompt
        text = tokenizer.apply_chat_template(
            [m.dict() for m in request.messages],
            tokenize=False,
            add_generation_prompt=True
        )
        model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

        generation_config = {
            "max_new_tokens": 512,
            "temperature": 0.8, # 控制生成文本的随机性。温度越高，生成的文本越随机和创造性；温度越低，文本越趋向于确定性和重复性。
            "top_k": 50, # 只从模型认为最可能的`k`个词中选择下一个词。`k`值越大，选择范围越广，生成的文本越多样；`k`值越小，选择范围越窄，生成的文本越趋向于高概率的词。
            "top_p": 0.9, # 从概率累计达到`p`的那一组词中随机选择下一个词。与Top-K不同，Top-P是动态的，依据每个上下文的不同而变化。
            "repetition_penalty": 1.2
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