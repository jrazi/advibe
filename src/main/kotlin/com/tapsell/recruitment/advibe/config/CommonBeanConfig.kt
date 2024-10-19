package com.tapsell.recruitment.advibe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
open class CommonBeanConfig {

    @Bean
    open fun objectMapper() : ObjectMapper {
        return ObjectMapper()
    }
}