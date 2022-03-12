package com.ziroom.framework.example.dubbo.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "APP收房合同信息")
public class ContractInfoRequest implements Serializable {

    private static final long serialVersionUID = -4554104403060860185L;

    /**
     * 合同号
     */
    @ApiModelProperty("收房合同号")
    private String hireContractCode;

    /**
     * 是否是线上合同
     */
    @ApiModelProperty("是否线上")
    private Integer isOnline;

    /**
     * 当前登录人系统号
     */
    @ApiModelProperty("当前登录人系统号")
    private String lastModifyCode;

    /**
     * 房屋信息
     */
    @ApiModelProperty("房屋信息")
    private HouseInfoRequest houseInfo;

    /**
     * 人员信息
     */
    @ApiModelProperty("人员信息")
    private List<OwnerInfoRequest> ownerInfos;
}
