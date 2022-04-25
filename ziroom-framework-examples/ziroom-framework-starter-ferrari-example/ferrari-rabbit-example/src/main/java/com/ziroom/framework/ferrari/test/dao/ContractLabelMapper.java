package com.ziroom.framework.ferrari.test.dao;

import com.ziroom.framework.ferrari.test.entity.ContractLabelEntity;
import org.apache.ibatis.annotations.Param;

public interface ContractLabelMapper {

    int insertSelective(ContractLabelEntity record);

    ContractLabelEntity selectByPrimaryKey(Long id);

    ContractLabelEntity selectByHireContractId(@Param("hireContractId") Long hireContractId);

    int updateByPrimaryKeySelective(ContractLabelEntity record);

    /**
     * 仅数据同步使用，业务不得使用
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);
}