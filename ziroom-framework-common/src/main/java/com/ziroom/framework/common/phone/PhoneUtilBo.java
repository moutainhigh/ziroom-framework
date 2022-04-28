package com.ziroom.framework.common.phone;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lijun
 * @Date: 2020/2/7 23:53
 */
@Data
@NoArgsConstructor
public class PhoneUtilBo {

    /**
     * 省会
     */
    private String provinceName;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 运营商
     */
    private String carrier;
}
