package com.isxcode.torch.api.work.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetDataSourceColumnsReq {

    @Schema(title = "数据源唯一id", example = "py_123456789")
    @NotEmpty(message = "数据源id不能为空")
    private String dataSourceId;

    @Schema(title = "待查询表名", example = "py_user")
    @NotEmpty(message = "表名不能为空")
    private String tableName;

}
