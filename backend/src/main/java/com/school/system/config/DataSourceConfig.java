package com.school.system.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;

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
        String rawUrl = env.getProperty("SPRING_DATASOURCE_URL");
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            rawUrl = env.getProperty("MYSQL_URL");
        }
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            rawUrl = env.getProperty("DATABASE_URL");
        }

        String username = env.getProperty("SPRING_DATASOURCE_USERNAME");
        if (username == null || username.trim().isEmpty()) {
            username = env.getProperty("MYSQLUSER");
        }

        String password = env.getProperty("SPRING_DATASOURCE_PASSWORD");
        if (password == null) {
            password = env.getProperty("MYSQLPASSWORD");
        }

        String host = env.getProperty("MYSQLHOST");
        String port = env.getProperty("MYSQLPORT");
        String database = env.getProperty("MYSQLDATABASE");

        String jdbcUrl;

        if (rawUrl != null && !rawUrl.trim().isEmpty()) {
            try {
                String cleanUrl = rawUrl.trim();
                if (cleanUrl.startsWith("jdbc:mysql://")) {
                    cleanUrl = cleanUrl.substring(5); // strip leading jdbc:
                }

                if (cleanUrl.startsWith("mysql://")) {
                    URI uri = new URI(cleanUrl.replace("mysql://", "http://"));
                    if (uri.getUserInfo() != null) {
                        String[] userInfo = uri.getUserInfo().split(":", 2);
                        if ((username == null || username.trim().isEmpty()) && userInfo.length > 0) {
                            username = userInfo[0];
                        }
                        if ((password == null || password.trim().isEmpty()) && userInfo.length > 1) {
                            password = userInfo[1];
                        }
                    }
                    String path = uri.getPath();
                    if (path == null || path.isEmpty() || "/".equals(path)) {
                        path = "/" + (database != null ? database : "school_db");
                    }
                    int p = uri.getPort() != -1 ? uri.getPort() : (port != null ? Integer.parseInt(port) : 3306);
                    jdbcUrl = "jdbc:mysql://" + uri.getHost() + ":" + p + path;
                } else {
                    jdbcUrl = rawUrl.startsWith("jdbc:") ? rawUrl : "jdbc:" + rawUrl;
                }
            } catch (Exception e) {
                log.warn("Failed to parse URL string using URI parser: {}", e.getMessage());
                jdbcUrl = rawUrl.startsWith("jdbc:") ? rawUrl : "jdbc:" + rawUrl;
            }
        } else {
            String h = host != null ? host : "localhost";
            String p = port != null ? port : "3306";
            String d = database != null ? database : "school_db";
            jdbcUrl = "jdbc:mysql://" + h + ":" + p + "/" + d;
        }

        if (username == null || username.trim().isEmpty()) {
            username = "root";
        }
        if (password == null) {
            password = "password";
        }

        // Append required MySQL flags if missing
        if (!jdbcUrl.contains("allowPublicKeyRetrieval")) {
            String separator = jdbcUrl.contains("?") ? "&" : "?";
            jdbcUrl = jdbcUrl + separator + "createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        }

        log.info("Configuring HikariDataSource with Clean JDBC URL: {}", jdbcUrl);
        log.info("Configuring Database User: {}", username);

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setInitializationFailTimeout(-1);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }
}
