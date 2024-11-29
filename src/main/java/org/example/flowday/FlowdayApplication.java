package org.example.flowday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@SpringBootApplication
@EnableJpaAuditing
public class FlowdayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowdayApplication.class, args);
    }

}
