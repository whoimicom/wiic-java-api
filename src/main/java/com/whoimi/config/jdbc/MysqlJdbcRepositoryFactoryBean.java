package com.whoimi.config.jdbc;

import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.MySqlDialect;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

public class MysqlJdbcRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends JdbcRepositoryFactoryBean<T, S, ID> {

    public MysqlJdbcRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    public void setDialect(Dialect dialect) {
        System.out.println(dialect);
        super.setDialect(MySqlDialect.INSTANCE);
    }


}