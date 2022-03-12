package com.ziroom.framework.service;

import com.ziroom.framework.common.api.pojo.ResponseData;
import com.ziroom.framework.example.dubbo.api.RentExampleFacade;
import com.ziroom.framework.example.dubbo.domain.reponse.PreviewContractInfoResponse;
import com.ziroom.framework.example.dubbo.domain.request.ContractInfoRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * Created by liangrk on 2022/3/12.
 */
@Service
public class HelloService {

    @DubboReference
    RentExampleFacade rentExampleFacade;

    public ResponseData<PreviewContractInfoResponse> hello() {
        ResponseData<PreviewContractInfoResponse> previewContractInfoResponseResponseData = rentExampleFacade.previewContractInfo(new ContractInfoRequest());
        return previewContractInfoResponseResponseData;
    }


}
