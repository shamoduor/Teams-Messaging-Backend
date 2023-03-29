package com.shamine.teamsmessagingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TeamsmessagingbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamsmessagingbackendApplication.class, args);
	}

}
