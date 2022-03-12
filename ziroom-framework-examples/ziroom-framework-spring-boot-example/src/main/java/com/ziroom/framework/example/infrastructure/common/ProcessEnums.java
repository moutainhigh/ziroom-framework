package com.ziroom.framework.example.infrastructure.common;


import com.ziroom.framework.constant.IProcess;

public enum ProcessEnums implements IProcess {

    SIGN_CONTRACT_CONFIRM,
    SIGN_PAY_PLAN,
    CONFIRM,
    WHITE;

    @Override
    public String getProcessName() {
        return name();
    }
}