package com.example.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@TestConfiguration
public class JooqTestConfig {

    @Value("${DB_URL}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${DB_PASSWORD}")
    private String password;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public DSLContext dsl(DataSource dataSource) { return DSL.using(dataSource, SQLDialect.POSTGRES); }
}
