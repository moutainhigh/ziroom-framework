package com.ziroom.framework.example.service.impl;

import com.ziroom.framework.example.config.MerakDataSourceConfig;
import com.ziroom.framework.example.dao.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *@description:用户角色表服务实现类
 *@author:liangrk
 *@date:2021-07-21
 *@since:1.0.0
 */
@Service
public class MerakServiceImpl{

//    @Autowired
//    @Qualifier("merakSqlSessionTemplate")
//    private SqlSessionTemplate merakSqlSessionTemplate;
//
//    @Autowired
//    @Qualifier("merakOmegaSqlSessionTemplate")
//    private SqlSessionTemplate merakOmegaSqlSessionTemplate;

//    public Long merakData(){
//        return merakSqlSessionTemplate.selectOne("com.ziroom.tech.omega.example.dao.RoleMapper.count");
//    }

    @Autowired
    private MerakDataSourceConfig merakDataSourceConfig;

    public Long merakData2(){
        RoleMapper mapper = merakDataSourceConfig.sqlSessionTemplate.getMapper(RoleMapper.class);
        return mapper.count();
    }
}
