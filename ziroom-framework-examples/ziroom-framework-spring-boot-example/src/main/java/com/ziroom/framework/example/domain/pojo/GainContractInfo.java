package com.ziroom.framework.example.domain.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "预览合同")
public class GainContractInfo implements Serializable {

    private static final long serialVersionUID = 7653246864325171843L;


    /**
     *  年总预期收益 -增益
     */
    @ApiModelProperty("年总预期收益 -增益")
    private BigDecimal totalAnnualIncome;

    /**
     *  每期收益 -增益
     */
    @ApiModelProperty("每期收益 -增益")
    private BigDecimal incomePerPeriod;

    /**
     *  每期分摊装修 -增益
     */
    @ApiModelProperty("每期分摊装修 -增益")
    private BigDecimal shareDecorationCostPerPeriod;

    /**
     *  每期管理服务费 -增益
     */
    @ApiModelProperty("每期管理服务费 -增益")
    private BigDecimal serviceFeePerPeriod;

}
