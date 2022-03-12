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
@ApiModel(description = "房屋信息")
public class HouseInfoRequest implements Serializable {

    private static final long serialVersionUID = -3717729692672473004L;

    /**
     * 产权类型
     */
    @ApiModelProperty("产权类型")
    private Integer propertyType;

    /**
     * 房屋所有权证书编号
     */
    @ApiModelProperty("房屋所有权证书编号")
    private String ownershipCode;

    /**
     * 产权地址
     */
    @ApiModelProperty("产权地址")
    private String propertyAddress;

    /**
     * 是否共有产权
     */
    @ApiModelProperty("是否共有产权")
    private Integer sharePropertyType;

    /**
     * 房屋是否抵押
     */
    @ApiModelProperty("房屋是否抵押")
    private Integer isPledge;

    /**
     * 房源用途
     */
    @ApiModelProperty("房源用途")
    private Integer housePurpose;

    /**
     * 统计用途编码
     */
    @ApiModelProperty("统计用途编码")
    private String statFunction;

    /**
     * 统计用途名称
     */
    @ApiModelProperty("统计用途名称")
    private String statFunctionName;

    /**
     * 交易权属
     */
    @ApiModelProperty("交易权属")
    private String dealProperty;

    /**
     * 供暖方式
     */
    @ApiModelProperty("供暖方式")
    private String supplyHeat;

    /**
     * 行政地址
     */
    @ApiModelProperty("行政地址")
    private String ratingAddress;

    /**
     * 是否安装壁挂式新风系统
     */
    @ApiModelProperty("是否安装壁挂式新风系统")
    private Integer isFreshAirSysInstall;

    /**
     * 公安标准地址
     */
    @ApiModelProperty("公安标准地址")
    private String policeRuleHouseAddress;

    /**
     * 标准地址编码
     */
    @ApiModelProperty("标准地址编码")
    private String standardAddressCode;

    /**
     * 公安房屋二维码
     */
    @ApiModelProperty("公安房屋二维码")
    private String houseTwoDimensionCodeUrl;

    /**
     * 是否有代理人
     */
    @ApiModelProperty("是否有代理人")
    private Integer hasClient;

    /**
     * 是否允许养宠物
     */
    //@ApiModelProperty("是否允许养宠物")
    private Integer isPermitPet;

    /**
     * 自住/出租证明类型
     */
    //@ApiModelProperty("自住/出租证明类型")
    private Integer exemptionType;

    /**
     * 房源编号
     */
    //@ApiModelProperty("房源编号")
    private String houseSourceCode;

    /**
     * 房屋产权照片
     */
    private String propertyRightPhotoUrl;

}
