package com.ziroom.framework.autoconfigure.rocketmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ziroom.ferrari.rocketmq.producer.FerrariRocketmqTemplate;
import com.ziroom.framework.autoconfigure.configx.ConfigXBeanRegistrar;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.autoconfigure.ListenerContainerConfiguration;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
public class RocketMQBeanRegistrar
    extends ConfigXBeanRegistrar<ZiroomRocketMQProperties> {

    public RocketMQBeanRegistrar() {
        super(ZiroomRocketMQProperties.class, "ziroom.rocketmq");
    }

    @Override
    protected void register(ZiroomRocketMQProperties configurationProperties, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        if (configurationProperties.getNameServer() == null) {
            return;
        }

        RocketMQProperties.Producer producer = configurationProperties.getProducer();

        // DefaultMQProducer
        if (producer != null && producer.getGroup() != null) {
            BeanDefinitionBuilder producerBuilder = BeanDefinitionBuilder.genericBeanDefinition(DefaultMQProducer.class);
            producerBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
            producerBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);

            producerBuilder.addConstructorArgValue(producer.getGroup());

            String ak = producer.getAccessKey();
            String sk = producer.getSecretKey();
            if (!StringUtils.isEmpty(ak) && !StringUtils.isEmpty(sk)) {
                producerBuilder.addConstructorArgValue(new AclClientRPCHook(new SessionCredentials(ak, sk)));
            }

            producerBuilder.addConstructorArgValue(producer.isEnableMsgTrace());
            producerBuilder.addConstructorArgValue(producer.getCustomizedTraceTopic());

            producerBuilder.addPropertyValue("namesrvAddr", configurationProperties.getNameServer());
            producerBuilder.addPropertyValue("sendMsgTimeout", producer.getSendMessageTimeout());
            producerBuilder.addPropertyValue("retryTimesWhenSendFailed", producer.getRetryTimesWhenSendFailed());
            producerBuilder.addPropertyValue("retryTimesWhenSendAsyncFailed", producer.getRetryTimesWhenSendAsyncFailed());
            producerBuilder.addPropertyValue("maxMessageSize", producer.getMaxMessageSize());
            producerBuilder.addPropertyValue("compressMsgBodyOverHowmuch", producer.getCompressMessageBodyThreshold());
            producerBuilder.addPropertyValue("retryAnotherBrokerWhenNotStoreOK", producer.isRetryNextServer());

            AbstractBeanDefinition beanDefinition = producerBuilder.getBeanDefinition();
            String producerBeanName = configurationProperties.getProducerBeanName() == null ?
                importBeanNameGenerator.generateBeanName(beanDefinition, registry) :
                configurationProperties.getProducerBeanName();
            registry.registerBeanDefinition(producerBeanName, beanDefinition);

            // 注册 RocketMQTemplate
            BeanDefinitionBuilder rmqTemplateBuilder;
            if (configurationProperties.getFerrari().isEnabled()) {
                rmqTemplateBuilder = BeanDefinitionBuilder.genericBeanDefinition(FerrariRocketmqTemplate.class);
            } else {
                rmqTemplateBuilder = BeanDefinitionBuilder.genericBeanDefinition(RocketMQTemplate.class);
            }

            AbstractBeanDefinition rmqTemplate = rmqTemplateBuilder
                .addAutowiredProperty("objectMapper")
                .addPropertyReference("producer", producerBeanName)
                .getBeanDefinition();
            String templateBeanName = configurationProperties.getTemplateBeanName() == null ?
                importBeanNameGenerator.generateBeanName(rmqTemplate, registry) :
                configurationProperties.getTemplateBeanName();
            registry.registerBeanDefinition(
                templateBeanName,
                rmqTemplate);
        }

    }

    @Override
    protected void afterBeanRegistration(List<ZiroomRocketMQProperties> list, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        // 如果只有一个 rocketmq 集群，
        if (list.size() == 1) {
            ZiroomRocketMQProperties rocketMQProperties = list.get(0);

            // 注册 ListenerContainerConfiguration
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ListenerContainerConfiguration.class);

            // TODO: Allow set custom object mapper
            builder.addConstructorArgValue(new ObjectMapper());
            builder.addConstructorArgValue(getEnvironment());
            builder.addConstructorArgValue(rocketMQProperties);

            AbstractBeanDefinition listenerContainerConfiguration = builder.getBeanDefinition();
            registry.registerBeanDefinition(
                importBeanNameGenerator.generateBeanName(listenerContainerConfiguration, registry), listenerContainerConfiguration);
        }
    }
}
