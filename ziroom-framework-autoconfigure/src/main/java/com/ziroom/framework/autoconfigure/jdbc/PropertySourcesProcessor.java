package com.ziroom.framework.autoconfigure.jdbc;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.*;

import java.util.*;

/**
 * Apollo Property Sources processor for Spring Annotation Based Application. <br /> <br />
 * <p>
 * The reason why PropertySourcesProcessor implements {@link BeanFactoryPostProcessor} instead of
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor} is that lower versions of
 * Spring (e.g. 3.1.1) doesn't support registering BeanDefinitionRegistryPostProcessor in ImportBeanDefinitionRegistrar
 * - {@link}
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class  PropertySourcesProcessor implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {
    private static final Multimap<Integer, String> NAMESPACE_NAMES = LinkedHashMultimap.create();
    private static final Set<BeanFactory> AUTO_UPDATE_INITIALIZED_BEAN_FACTORIES = Sets.newConcurrentHashSet();

    private final ConfigPropertySourceFactory configPropertySourceFactory = SpringInjector
            .getInstance(ConfigPropertySourceFactory.class);
    private final ConfigUtil configUtil = ApolloInjector.getInstance(ConfigUtil.class);
    private ConfigurableEnvironment environment;

    public static boolean addNamespaces(Collection<String> namespaces, int order) {
        return NAMESPACE_NAMES.putAll(order, namespaces);
    }
    public static boolean addNamespaces(String namespace, int order) {

        return NAMESPACE_NAMES.put(order, namespace);
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        String remote = Foundation.getProperty("remote", "true");
//        if(!Boolean.valueOf(remote)){
//            System.out.println("DISABLED REMOTE CONFIG, REMOTE IS:"+remote);
//            return;
//        }
        initializePropertySources();
        initializeAutoUpdatePropertiesFeature(beanFactory);
    }

    private void initializePropertySources() {
        if (environment.getPropertySources().contains(PropertySourcesConstants.APOLLO_PROPERTY_SOURCE_NAME)) {
            //already initialized
            return;
        }
        CompositePropertySource composite = new CompositePropertySource(PropertySourcesConstants.APOLLO_PROPERTY_SOURCE_NAME);

        //sort by order asc
        ImmutableSortedSet<Integer> orders = ImmutableSortedSet.copyOf(NAMESPACE_NAMES.keySet());
        Iterator<Integer> iterator = orders.iterator();

//        while (iterator.hasNext()) {
//            int order = iterator.next();
//            for (String namespace : NAMESPACE_NAMES.get(order)) {
//                Config config = ConfigService.getConfig(namespace);
//
//                composite.addPropertySource(configPropertySourceFactory.getConfigPropertySource(namespace, config));
//            }
//        }
//
//        // clean up
//        NAMESPACE_NAMES.clear();
//
//        // add after the bootstrap property source or to the first
//        if (environment.getPropertySources()
//                .contains(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
//
//            // ensure ApolloBootstrapPropertySources is still the first
//            ensureBootstrapPropertyPrecedence(environment);
//
//            environment.getPropertySources()
//                    .addAfter(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME, composite);
//        } else {
//            environment.getPropertySources().addFirst(composite);
//        }
//        composite.addPropertySource(configPropertySourceFactory.getConfigPropertySource(namespace, config));
        Properties properties = new Properties();
        //    name: merak
        //    driver-class-name: com.mysql.cj.jdbc.Driver
        //    url: jdbc:mysql://10.30.7.135:3306/merak?useUnicode=true&serverTimezone=GMT&useSSL=false&characterEncoding=utf8
        //    username: dev_jcpt
        //    password: ziroomdb
        properties.setProperty("spring.datasource.driver-class-name","com.mysql.cj.jdbc.Driver");
        properties.setProperty("spring.datasource.url","jdbc:mysql://10.30.7.135:3306/merak?useUnicode=true&serverTimezone=GMT&useSSL=false&characterEncoding=utf8");
        properties.setProperty("spring.datasource.username","dev_jcpt");
        properties.setProperty("spring.datasource.password","ziroomdb");

        PropertiesPropertySource source = new PropertiesPropertySource("merak",properties);
        environment.getPropertySources().addFirst(source);
    }

    private void ensureBootstrapPropertyPrecedence(ConfigurableEnvironment environment) {
        MutablePropertySources propertySources = environment.getPropertySources();

        PropertySource<?> bootstrapPropertySource = propertySources
                .get(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME);

        // not exists or already in the first place
        if (bootstrapPropertySource == null || propertySources.precedenceOf(bootstrapPropertySource) == 0) {
            return;
        }

        propertySources.remove(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME);
        propertySources.addFirst(bootstrapPropertySource);
    }

    private void initializeAutoUpdatePropertiesFeature(ConfigurableListableBeanFactory beanFactory) {
        if (!configUtil.isAutoUpdateInjectedSpringPropertiesEnabled() ||
                !AUTO_UPDATE_INITIALIZED_BEAN_FACTORIES.add(beanFactory)) {
            return;
        }

//    AutoUpdateConfigChangeListener autoUpdateConfigChangeListener = new AutoUpdateConfigChangeListener(
//        environment, beanFactory);
        AutoUpdateConfigChangeListener autoUpdateConfigChangeListener = new AutoUpdateConfigChangeListener(
                environment, beanFactory);
        List<ConfigPropertySource> configPropertySources = configPropertySourceFactory.getAllConfigPropertySources();
        for (ConfigPropertySource configPropertySource : configPropertySources) {
            configPropertySource.addChangeListener(autoUpdateConfigChangeListener);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        //it is safe enough to cast as all known environment is derived from ConfigurableEnvironment
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public int getOrder() {
        //make it as early as possible
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
