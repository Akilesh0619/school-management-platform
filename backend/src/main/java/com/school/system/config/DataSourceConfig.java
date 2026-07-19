package com.school.system.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class DataSourceConfig {

    private final Environment env;

    public DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        String url = env.getProperty("SPRING_DATASOURCE_URL");
        if (url == null || url.trim().isEmpty()) {
            url = env.getProperty("MYSQL_URL");
        }
        if (url == null || url.trim().isEmpty()) {
            url = env.getProperty("DATABASE_URL");
        }

        if (url == null || url.trim().isEmpty()) {
            String host = env.getProperty("MYSQLHOST", "localhost");
            String port = env.getProperty("MYSQLPORT", "3306");
            String db = env.getProperty("MYSQLDATABASE", "school_db");
            url = "jdbc:mysql://" + host + ":" + port + "/" + db;
        }

        // Fix Railway's default mysql:// scheme to jdbc:mysql://
        if (url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }

        // Append required MySQL flags if missing
        if (!url.contains("allowPublicKeyRetrieval")) {
            String separator = url.contains("?") ? "&" : "?";
            url = url + separator + "createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        }

        String username = env.getProperty("SPRING_DATASOURCE_USERNAME");
        if (username == null || username.trim().isEmpty()) {
            username = env.getProperty("MYSQLUSER", "root");
        }

        String password = env.getProperty("SPRING_DATASOURCE_PASSWORD");
        if (password == null) {
            password = env.getProperty("MYSQLPASSWORD", "password");
        }

        log.info("Configuring HikariDataSource for Railway JDBC URL: {}", url);
        log.info("Configuring Database User: {}", username);

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setInitializationFailTimeout(-1); // Do not crash Spring context startup if DB connection is delayed
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }
}
