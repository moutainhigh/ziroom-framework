package com.ziroom.framework.example.domain.reponse;

import com.ziroom.framework.example.domain.pojo.GainContractInfo;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "预览合同")
public class PreviewContractInfoResponse implements Serializable {

    private static final long serialVersionUID = 7653246864325171843L;

    /**
     * 预览合同url
     */
    private String contractUrl;

    private GainContractInfo gainContractInfo;

}
