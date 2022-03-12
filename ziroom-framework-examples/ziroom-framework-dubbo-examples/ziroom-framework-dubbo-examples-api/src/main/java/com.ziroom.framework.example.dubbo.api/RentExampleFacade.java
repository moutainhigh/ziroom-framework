package com.ziroom.framework.example.dubbo.api;


import com.ziroom.framework.common.api.pojo.ResponseData;
import com.ziroom.framework.example.dubbo.domain.reponse.AppEnumResponse;
import com.ziroom.framework.example.dubbo.domain.reponse.PreviewContractInfoResponse;
import com.ziroom.framework.example.dubbo.domain.request.ContractInfoRequest;

public interface RentExampleFacade {
    ResponseData<AppEnumResponse> getEnumList();
    ResponseData<PreviewContractInfoResponse> previewContractInfo(ContractInfoRequest contractInfoRequest);
}
