package com.example.anonymizer.config;

import com.example.anonymizer.processor.CapNProtoDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DecoderConfig {
    @Bean
    public CapNProtoDecoder DecoderConfig() {
        return new CapNProtoDecoder();
    }
}
