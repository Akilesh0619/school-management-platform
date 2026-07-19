package com.school.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String url = properties.getUrl();
        log.info("Initializing DataSource with URL pattern: {}", url);

        if (url != null) {
            // Fix Railway's default mysql:// scheme to jdbc:mysql://
            if (url.startsWith("mysql://")) {
                url = "jdbc:" + url;
            }
            // Append required MySQL flags if missing
            if (!url.contains("allowPublicKeyRetrieval")) {
                String separator = url.contains("?") ? "&" : "?";
                url = url + separator + "createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            }
            properties.setUrl(url);
            log.info("Sanitized JDBC URL for Railway MySQL: {}", url);
        }

        return properties.initializeDataSourceBuilder().build();
    }
}
