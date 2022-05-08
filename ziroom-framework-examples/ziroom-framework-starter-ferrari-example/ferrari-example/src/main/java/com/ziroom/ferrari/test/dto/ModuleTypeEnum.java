package com.ziroom.ferrari.test.dto;

import lombok.Getter;

import java.util.Objects;

/**
 * @Author: Administrator
 * @CreateTime: 2021-07-07 17:06
 * @Description: 卡片模块
 */
@Getter
public enum ModuleTypeEnum {
    broadband("broadband", "broadband"),
    change_rent("change_rent", "change_rent");

    private String moduleType;

    private String beanName;

    ModuleTypeEnum(String moduleType, String beanName) {
        this.moduleType = moduleType;
        this.beanName = beanName;
    }

    public static ModuleTypeEnum getEnumByModuleType(String moduleType) {
        for (ModuleTypeEnum moduleTypeEnum : ModuleTypeEnum.values()) {
            if (Objects.equals(moduleTypeEnum.getModuleType(), moduleType)) {
                return moduleTypeEnum;
            }
        }
        return null;
    }
}
