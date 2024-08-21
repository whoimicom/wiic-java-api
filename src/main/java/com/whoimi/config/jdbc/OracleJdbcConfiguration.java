package com.whoimi.config.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableJdbcRepositories("com.whoimi.repository.oracle")
public class OracleJdbcConfiguration {

    @Bean

    @ConfigurationProperties(prefix = "spring.datasource.oracle")
    public DataSource oracleServerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    NamedParameterJdbcOperations jdbcOperations(@Qualifier("oracleServerDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}