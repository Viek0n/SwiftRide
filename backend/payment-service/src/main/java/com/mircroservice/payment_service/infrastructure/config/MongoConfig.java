package com.mircroservice.payment_service.infrastructure.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration 
@EnableMongoAuditing 
public class MongoConfig {
    @Bean
    public BeanPostProcessor mappingMongoConverterPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(
                    Object bean, String beanName) {

                if (bean instanceof MappingMongoConverter converter) {
                    converter.setTypeMapper(
                            new DefaultMongoTypeMapper(null)
                    );
                }

                return bean;
            }
        };
    }
}
