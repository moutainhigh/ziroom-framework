package com.ziroom.framework.example.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

///**
// * 主数据源配置
// *
// * @author niujinpeng
// * @website: https://www.wdbyte.com
// * @date 2020/12/19
// */
@Configuration
//@MapperScan(basePackages = {"com.ziroom.tech.omega.example.dao"}, sqlSessionFactoryRef = "merakSqlSessionFactory")

public class MerakOmegaDataSourceConfig {

    @Autowired
    private BeanFactory beanFactory;

//    @Bean(name = "merakOmegaSqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactory() throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource((DataSource)beanFactory.getBean("merakOmega"));
////        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
//        return bean.getObject();
//    }
//
//    @Bean(name = "merakOmegaTransactionManager")
//    public DataSourceTransactionManager transactionManager() {
//        return new DataSourceTransactionManager((DataSource)beanFactory.getBean("merakOmega"));
//    }
//
//
//    @Bean(name = "merakOmegaSqlSessionTemplate")
//    public SqlSessionTemplate sqlSessionTemplate() throws Exception{
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource((DataSource)beanFactory.getBean("merakOmega"));
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
//        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) bean.getObject();
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
}