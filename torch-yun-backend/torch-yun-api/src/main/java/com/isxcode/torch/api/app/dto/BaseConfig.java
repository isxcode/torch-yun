package com.isxcode.torch.api.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseConfig {

    @Schema(title = "maxTokens", example = "限制生成文本的最大长度")
    private Integer maxTokens;

    @Schema(title = "seed", example = "设置随机数种子，控制生成内容的随机性和可重复性")
    private Integer seed;

    @Schema(title = "topK", example = "限制候选词汇的数量，控制生成内容的质量和多样性")
    private Integer topK;

    @Schema(title = "topP", example = "动态限制候选词汇的累计概率范围，控制生成内容的质量和多样性")
    private Double topP;

    @Schema(title = "temperature", example = "调整概率分布的平滑程度")
    private Float temperature;

    @Schema(title = "repetitionPenalty", example = "惩罚重复内容，减少生成文本中的重复现象")
    private Float repetitionPenalty;

    @Schema(title = "enableSearch", example = "启用搜索功能")
    private Boolean enableSearch;

}
