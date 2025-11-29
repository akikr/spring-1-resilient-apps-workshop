package io.javabrains.spring_7_workshop_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;

@EnableResilientMethods
@SpringBootApplication
public class Spring7Workshop1Application {

	public static void main(String[] args) {
		SpringApplication.run(Spring7Workshop1Application.class, args);
	}

}
