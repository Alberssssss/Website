package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:version.properties")
public class VersionConfig {

    @Value("${app.version}")
    private String version;

    @Value("${build.time}")
    private String buildTime;

    @Bean
    public VersionInfo versionInfo() {
        return new VersionInfo(version, buildTime);
    }
}