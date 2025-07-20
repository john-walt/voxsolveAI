package com.b2b.AIhelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication // Specify the package where your entities are located
public class AIhelperApplication {

	public static void main(String[] args) {
		SpringApplication.run(AIhelperApplication.class, args);
	}

}
