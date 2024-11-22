package org.example.flowday.domain.member.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public TestRestTemplate testRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return new TestRestTemplate(restTemplateBuilder);
    }
}
