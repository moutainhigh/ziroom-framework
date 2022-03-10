package com.ziroom.framework.example.infrastructure.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * <p>
 * 错误码
 * 用户可见编码，统一格式：A_BB_CCC
 * A:错误级别，如S代表系统级错误，E代表服务级错误；
 * B:项目或模块名称；
 * C:具体错误编号，自增即可；
 * </p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
@SuppressWarnings("serial")
@ConfigurationProperties(prefix = "api.errcode.map")
public class ApiErrorCodeProperties implements Serializable {
    private String E_01_000;
    private String E_02_001;
    private String E_03_000;
}

