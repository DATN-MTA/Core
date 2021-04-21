package edu.mta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MTAServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MTAServerApplication.class, args);
	}

}
