package com.ziroom.framework.example.dubbo.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "人员信息")
public class OwnerInfoRequest implements Serializable {

    private static final long serialVersionUID = -407809431109020254L;

    /**
     * 签约主体
     */
    @ApiModelProperty("签约主体")
    private Integer contractSubject;

    /**
     * 人员类型
     */
    @ApiModelProperty("人员类型")
    private Integer ownerType;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String name;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String mobile;

    /**
     * 证件类型
     */
    @ApiModelProperty("证件类型")
    private Integer paperType;

    /**
     * 证件号码
     */
    @ApiModelProperty("证件号码")
    private String paperCode;

    /**
     * 签发机关
     */
    @ApiModelProperty("签发机关")
    private String issueAuthority;

    /**
     * 证件是否长期有效
     */
    @ApiModelProperty("证件是否长期有效")
    private Integer isInfiniteEffect;

    /**
     * 证件有效起始日期
     */
    @ApiModelProperty("证件有效起始日期")
    private String effectiveStartDate;

    /**
     * 证件有效截止日期
     */
    @ApiModelProperty("证件有效截止日期")
    private String effectiveEndDate;

    /**
     * 通讯地址
     */
    @ApiModelProperty("通讯地址信息")
    private String address;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱信息")
    private String email;

    /**
     * 生日
     */
    @ApiModelProperty("生日")
    private String birthday;

    /**
     * cuid
     */
    @ApiModelProperty("cuid信息")
    private String cuid;

    /**
     * 证件正面照片
     */
    private String paperFrontUrl;

    /**
     * 证件反面照片
     */
    private String paperBackUrl;
    /**
     * 国籍
     */
    private String nationality;

    /**
     * 国籍编码
     */
    private String nationalityCode;


}
