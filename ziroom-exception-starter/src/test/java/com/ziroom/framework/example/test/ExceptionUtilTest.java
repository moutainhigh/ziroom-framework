package com.ziroom.framework.example.test;

import com.ziroom.framework.base.DataErrorException;
import com.ziroom.framework.constant.ExceptionTypeEnity;
import com.ziroom.framework.example.test.mock.ErrorCodeEnums;
import com.ziroom.framework.example.test.mock.LogPrintImpl;
import com.ziroom.framework.example.test.mock.ProcessEnums;
import com.ziroom.framework.example.test.mock.SystemImpl;
import com.ziroom.framework.util.ExceptionUtil;
import org.springframework.util.Assert;

public class ExceptionUtilTest {
    public void exceptionUtilTest() {
        boolean expression = false;
        SystemImpl systemConfig = new SystemImpl();
        LogPrintImpl logPrint = new LogPrintImpl();
        try {
            ExceptionUtil.checkDataError(
                    expression,
                    406,
                    logPrint,
                    ExceptionTypeEnity.builder()
                            .system(systemConfig)
                            .process(ProcessEnums.CONFIRM)
                            .errorCode(ErrorCodeEnums.ERROR_406_CODE)
                            .build(),
                    "抱歉，房源状态不符合出租条件，该房源可能已经被其他用户下定，或检查我的租约菜单。id={}",
                    ""
            );
            Assert.isTrue(false, "异常没有抛出");
        } catch (DataErrorException ex) {
            if (406 == ex.getCode()) {
                Assert.isTrue(true, "OK");
            } else {
                Assert.isTrue(false, "code不符");
            }
        }
    }
}
