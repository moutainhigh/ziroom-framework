package com.ziroom.framework.example.test.mock;

import com.ziroom.framework.constant.ISystem;

public class SystemImpl implements ISystem {
    @Override
    public String getSystem() {
        return "CRM";
    }
}
