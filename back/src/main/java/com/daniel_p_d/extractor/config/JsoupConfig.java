package com.daniel_p_d.extractor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsoupConfig {
    
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36";

    public static final int TIMEOUT_MS = 15000;

    @Bean
    public JsoupConnector jsoupConnector(){
        return new JsoupConnector(USER_AGENT, TIMEOUT_MS);
    }
    
}
