package com.rtp.orderservice.config;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class EmbeddedPostgresConfig {

    @Bean
    public DataSource dataSource() throws IOException {

        EmbeddedPostgres postgres = EmbeddedPostgres.start();
        return postgres.getPostgresDatabase();
    }
}
