package com.ziroom.framework.example.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

///**
// * 主数据源配置
// *
// * @author niujinpeng
// * @website: https://www.wdbyte.com
// * @date 2020/12/19
// */
//@Configuration
//@MapperScan(basePackages = {"com.ziroom.tech.omega.example.dao"}, sqlSessionFactoryRef = "merakSqlSessionFactory")

@Configuration
public class MerakDataSourceConfig  {

//    @Autowired
//    private BeanFactory beanFactory;
    public SqlSessionTemplate sqlSessionTemplate;

    @Autowired(required = false)
    @Qualifier("merak")
    private DataSource dataSource;

    @Primary
    @Bean(name = "merakSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return bean.getObject();
    }
//
////    @Primary
//    @Bean(name = "merakTransactionManager")
//    public DataSourceTransactionManager transactionManager() {
//        return new DataSourceTransactionManager((DataSource)beanFactory.getBean("merak"));
//    }

//    @Primary
//    @Bean(name = "merakSqlSessionTemplate")
//    public SqlSessionTemplate sqlSessionTemplate() throws Exception{
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource((DataSource)beanFactory.getBean("merak"));
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
//        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) bean.getObject();
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }


//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

//        if (bean instanceof DataSource && "merak".equals(beanName)){
//            this.dataSource = (DataSource) bean;
//        }
//        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
//    }
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        System.out.println(dataSource);
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource((DataSource)beanFactory.getBean("merak"));
//        try {
//            bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        SqlSessionFactory sqlSessionFactory = null;
//        try {
//            sqlSessionFactory = (SqlSessionFactory) bean.getObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
//    }
}