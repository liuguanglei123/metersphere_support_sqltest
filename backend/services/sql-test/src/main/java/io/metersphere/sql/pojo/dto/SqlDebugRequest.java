package io.metersphere.sql.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SqlDebugRequest {
    @Schema(description = "模块ID(根据模块树查询时要把当前节点以及子节点都放在这里。)")
    private List<@NotBlank String> moduleIds;

    @Schema(description = "关键字")
    private String keyword;
}
