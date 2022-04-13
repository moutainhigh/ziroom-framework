package com.ziroom.framework.autoconfigure.jdbc;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class OmegaConfigRegistrar implements ImportBeanDefinitionRegistrar{


  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, PropertySourcesProcessor.class.getName(),
        PropertySourcesProcessor.class);

  }
}
