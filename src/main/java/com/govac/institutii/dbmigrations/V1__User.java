package com.govac.institutii.dbmigrations;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1__User implements SpringJdbcMigration {
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute("CREATE TABLE users (id bigserial PRIMARY KEY, username varchar(20) NOT NULL, password text NOT NULL, email varchar(120) NOT NULL)");
    }
}
