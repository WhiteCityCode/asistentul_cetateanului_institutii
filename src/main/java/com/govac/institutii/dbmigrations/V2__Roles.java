package com.govac.institutii.dbmigrations;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2__Roles implements SpringJdbcMigration {
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute("ALTER TABLE users ADD COLUMN role varchar(10) CHECK (role IN ('admin', 'provider', 'cetatean'));");
    }
}