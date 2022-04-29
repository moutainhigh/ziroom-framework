package com.ziroom.ferrari.test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author wangyanyong
 * @date 2021/7/7 10:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户id不能为空")
    @JsonProperty("uid")
    private String uid;

    @JsonProperty("rentContractCode")
    private String rentContractCode;

    @NotNull(message = "模块类型不能为空")
    @JsonProperty("moduleType")
    private ModuleTypeEnum moduleType;

    @NotBlank(message = "应用名称不能为空")
    @JsonProperty("appId")
    private String appId;
}
