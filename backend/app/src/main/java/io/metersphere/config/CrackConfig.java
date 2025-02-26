package io.metersphere.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 破解，手动在数据库中增加的用户，无法正常登录ms，发现是因为CFTVolumeLimitation切面导致校验cftoken失败，因此定义一个bean注册的PostProcessor
 * 将CFTVolumeLimitation切面类从spring中剔除
 */
@Configuration
public class CrackConfig {
    @Bean
    public static BeanDefinitionRegistryPostProcessor registryPostProcessor() {
        return new BeanDefinitionRegistryPostProcessor() {
            @Override
            public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
                registry.removeBeanDefinition("CFTVolumeLimitation");
            }

            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            }
        };
    }
}
