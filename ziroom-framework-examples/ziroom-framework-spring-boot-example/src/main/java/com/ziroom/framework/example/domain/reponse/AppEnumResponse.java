package com.ziroom.framework.example.domain.reponse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "APP枚举")
public class AppEnumResponse implements Serializable {

    private static final long serialVersionUID = 5280647008609069649L;

    /**
     * 版本号
     */
    @ApiModelProperty("版本号")
    private String version;

    /**
     * 枚举列表
     */
    @ApiModelProperty("枚举列表")
    private Map<String, List<String>> enums;

}
