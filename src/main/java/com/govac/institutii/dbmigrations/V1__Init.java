package com.govac.institutii.dbmigrations;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1__Init implements SpringJdbcMigration {
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute("CREATE TABLE users(id bigserial PRIMARY KEY"
        		+ ", cnp varchar(20) NOT NULL"
        		+ ", email varchar(80) NOT NULL"
        		+ ", first_name varchar(80) NOT NULL"
        		+ ", last_name varchar(80) NOT NULL"
        		+ ", phone varchar(20)"
        		+ ");");
        
        jdbcTemplate.execute("CREATE TABLE user_profiles(id bigserial PRIMARY KEY"
        		+ ", user_id bigint NOT NULL"
        		+ ", profile jsonb NOT NULL"
        		+ ", CONSTRAINT fk_profile_users FOREIGN KEY (user_id) "
        			+ "REFERENCES users (id) "
        			+ "MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"
        		+ ");");
        jdbcTemplate.execute("CREATE INDEX idxgin_profile ON user_profiles USING gin (profile);");
        
        jdbcTemplate.execute("CREATE TABLE providers (id bigserial PRIMARY KEY"
        		+ ", admin_id bigint NOT NULL"
        		+ ", name varchar(80) NOT NULL"
        		+ ", url varchar(200) NOT NULL"
        		+ ", CONSTRAINT fk_providers_users FOREIGN KEY (admin_id) "
        			+ "REFERENCES users (id) "
        			+ "MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"
        			+ ");");
        
        jdbcTemplate.execute("CREATE TABLE applications (id serial PRIMARY KEY"
        		+ ", provider_id bigint NOT NULL"
        		+ ", name varchar(80) NOT NULL"
        		+ ", description text"
        		+ ", tkn varchar(80) NOT NULL"
        		+ ", requirements jsonb NOT NULL"
        		+ ", CONSTRAINT fk_applications_providers FOREIGN KEY (provider_id) "
        			+ "REFERENCES providers (id) "
        			+ "MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"
        		+ ");");
        jdbcTemplate.execute("CREATE INDEX idxgin_requirements ON applications USING gin (requirements);");
        
        jdbcTemplate.execute("CREATE TABLE subscriptions (id bigserial PRIMARY KEY"
        		+ ", user_id bigint NOT NULL"
        		+ ", application_id int NOT NULL"
        		+ ", subscribed timestamp NOT NULL"
        		+ ", CONSTRAINT fk_subscriptions_users FOREIGN KEY (user_id) "
        			+ "REFERENCES users (id) "
        			+ "MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"
        		+ ", CONSTRAINT fk_subscriptions_applications FOREIGN KEY (application_id) "
        			+ "REFERENCES applications (id) "
        			+ "MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"
        		+ ");");
        
        jdbcTemplate.execute(
        		"CREATE TABLE notifications ( id bigserial PRIMARY KEY"
        		+ ", application_id int NOT NULL"
        		+ ", title varchar(80) NOT NULL"
        		+ ", description text NOT NULL"
        		+ ", short_description varchar(100)"
        		+ ", metadata jsonb NOT NULL"
        		+ ", CONSTRAINT fk_notifications_applications FOREIGN KEY (application_id) "
        			+ "REFERENCES applications (id) "
        			+ "MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"
				+ ");");
        jdbcTemplate.execute("CREATE INDEX idxgin_metadata ON notifications USING gin (metadata);");
    }
}
