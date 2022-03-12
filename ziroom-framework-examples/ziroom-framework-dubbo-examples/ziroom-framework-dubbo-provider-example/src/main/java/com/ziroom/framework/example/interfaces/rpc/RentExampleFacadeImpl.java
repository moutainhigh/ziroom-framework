package com.ziroom.framework.example.interfaces.rpc;

import com.ziroom.framework.common.api.pojo.ResponseData;
import com.ziroom.framework.example.dubbo.api.RentExampleFacade;
import com.ziroom.framework.example.dubbo.domain.reponse.AppEnumResponse;
import com.ziroom.framework.example.dubbo.domain.reponse.PreviewContractInfoResponse;
import com.ziroom.framework.example.dubbo.domain.request.ContractInfoRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(validation = "true")
@Slf4j
@Api("app收房新签Dubbo样例")
public class RentExampleFacadeImpl implements RentExampleFacade {

    @Override
    @ApiOperation("预览合同信息")
    public ResponseData<PreviewContractInfoResponse> previewContractInfo(ContractInfoRequest contractInfoRequest) {
        PreviewContractInfoResponse response = new PreviewContractInfoResponse();
        response.setContractUrl("hello world");
        return ResponseData.success(response);
    }

    @Override
    @ApiOperation("获取app枚举列表")
    public ResponseData<AppEnumResponse> getEnumList() {
        AppEnumResponse appEnumResponse = new AppEnumResponse();
        return ResponseData.success(appEnumResponse);
    }


}
