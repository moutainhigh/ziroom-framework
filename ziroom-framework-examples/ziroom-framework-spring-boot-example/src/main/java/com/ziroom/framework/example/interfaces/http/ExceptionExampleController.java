package com.ziroom.framework.example.interfaces.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
public class ExceptionExampleController {
//    @Resource
//    private SystemConfig systemConfig;

//    @RequestMapping(value = "/exception/businessException1", method = {RequestMethod.GET}, produces = "application/json;charset=UTF-8")
//    @CrossOrigin(methods = {RequestMethod.GET}, origins = "*")
//    @ResponseBody
//    public String businessException1(@RequestBody String json) {
//        throw new BusinessException(503, "example businessException sucess!");
//    }
//
//    @RequestMapping(value = "/exception/businessException2", method = {RequestMethod.GET}, produces = "application/json;charset=UTF-8")
//    @CrossOrigin(methods = {RequestMethod.GET}, origins = "*")
//    @ResponseBody
//    public String businessException2(@RequestBody String json) {
//        throw new BusinessException(200, "example businessException sucess!");
//    }
//
//    @RequestMapping(value = "/exception/systemException", method = {RequestMethod.GET}, produces = "application/json;charset=UTF-8")
//    @CrossOrigin(methods = {RequestMethod.GET}, origins = "*")
//    @ResponseBody
//    public String systemException(@RequestBody String json) {
//        throw new SystemException(504, "example systemException sucess!");
//    }
//
//    @RequestMapping(value = "/exception/thirdPartException", method = {RequestMethod.GET}, produces = "application/json;charset=UTF-8")
//    @CrossOrigin(methods = {RequestMethod.GET}, origins = "*")
//    @ResponseBody
//    public String thirdPartException(@RequestBody String json) {
//        throw new ThirdPartException(505, "example thirdPartException sucess!");
//    }
//
//    @RequestMapping(value = "/exception/exceptionUtilTest", method = {RequestMethod.GET}, produces = "application/json;charset=UTF-8")
//    @CrossOrigin(methods = {RequestMethod.GET}, origins = "*")
//    @ResponseBody
//    public String exceptionUtilTest() {
//        boolean expression = false;
//        ExceptionUtil.checkDataError(expression,
//                406,
//                () -> log.error("抱歉，房源状态不符合出租条件，该房源可能已经被其他用户下定，或检查我的租约菜单。"),
//                ExceptionTypeEnity.builder()
//                        .system(systemConfig)
//                        .process(ProcessEnums.CONFIRM)
//                        .errorCode(ErrorCodeEnums.ERROR_DEFAULT_CODE)
//                        .build(),
//                "抱歉，房源状态不符合出租条件，该房源可能已经被其他用户下定，或检查我的租约菜单。",
//                ""
//        );
//        return "OK";
//    }

}
