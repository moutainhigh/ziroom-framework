package com.ziroom.framework.example.interfaces.rpc;

import com.ziroom.framework.api.pojo.ResponseData;
import com.ziroom.framework.example.domain.reponse.AppEnumResponse;
import com.ziroom.framework.example.domain.reponse.PreviewContractInfoResponse;
import com.ziroom.framework.example.domain.request.ContractInfoRequest;

public interface RentExampleFacade {
    ResponseData<AppEnumResponse> getEnumList();
    ResponseData<PreviewContractInfoResponse> previewContractInfo(ContractInfoRequest contractInfoRequest);
}
