package com.medisphere.appointment;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MedisphereAppointmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedisphereAppointmentServiceApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public Gson gson() {
		return new Gson();
	}

}
