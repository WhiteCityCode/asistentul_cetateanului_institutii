package com.govac.institutii;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class KafkaConfiguration {
	
	@Bean
    public Properties kafkaProperties() throws IOException {
		Properties props = new Properties();
		props.load(new ClassPathResource("kafka.properties").getInputStream());		
        return props;
    }
}
