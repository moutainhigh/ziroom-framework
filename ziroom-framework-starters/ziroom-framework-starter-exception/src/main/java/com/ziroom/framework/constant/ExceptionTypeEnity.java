package com.ziroom.framework.constant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionTypeEnity {
    private final String SPLIT_CHAR = "_";
    ISystem system;
    IProcess process;
    IErrorCode errorCode;

    /**
     * 错误码， 用户可见编码
     * key = 统一格式：A_BB_CCC
     * A:错误级别，如S代表系统级错误，E代表业务级错误；
     * B:项目或模块名称；
     * C:具体错误编号，自增即可；
     * <p>
     */
    public String createCode(){
        return (new StringBuffer())
                .append(system.getSystem()).append(SPLIT_CHAR)
                .append(process.getProcessName()).append(SPLIT_CHAR)
                .append(errorCode.getCode())
                .toString();
    }
}
