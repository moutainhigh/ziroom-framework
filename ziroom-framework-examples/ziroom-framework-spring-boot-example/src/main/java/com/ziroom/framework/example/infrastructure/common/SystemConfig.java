package com.ziroom.framework.example.infrastructure.common;

import com.ziroom.framework.constant.ISystem;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConfig implements ISystem {
    @Override
    public String getSystem() {
        return SystemEnum.CRM.name();
    }
}
