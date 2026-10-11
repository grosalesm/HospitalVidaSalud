package com.vidasalud.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CitasServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CitasServiceApplication.class, args);
	}

}
