package org.blinkapp;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Configuration
@ComponentScan
public class ApplicationConfiguration {

    @Value("${DB_URL")
    private String url;

    @Value("${DB_USERNAME}")
    private String username;

    @Value("${DB_PASSWORD")
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
    public DSLContext dslContext(DataSource dataSource) { return DSL.using(dataSource, SQLDialect.POSTGRES);}

}
