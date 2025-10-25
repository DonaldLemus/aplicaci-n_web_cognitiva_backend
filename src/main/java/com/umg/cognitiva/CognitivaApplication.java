package com.umg.cognitiva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CognitivaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CognitivaApplication.class, args);
	}

}
