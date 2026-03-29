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
		return new com.google.gson.GsonBuilder()
				.registerTypeAdapter(java.time.LocalDate.class,
						(com.google.gson.JsonSerializer<java.time.LocalDate>) (src, typeOfSrc,
								context) -> new com.google.gson.JsonPrimitive(
										src.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)))
				.registerTypeAdapter(java.time.LocalDate.class,
						(com.google.gson.JsonDeserializer<java.time.LocalDate>) (json, typeOfT,
								context) -> java.time.LocalDate.parse(json.getAsString(),
										java.time.format.DateTimeFormatter.ISO_LOCAL_DATE))
				.registerTypeAdapter(java.time.LocalTime.class,
						(com.google.gson.JsonSerializer<java.time.LocalTime>) (src, typeOfSrc,
								context) -> new com.google.gson.JsonPrimitive(
										src.format(java.time.format.DateTimeFormatter.ISO_LOCAL_TIME)))
				.registerTypeAdapter(java.time.LocalTime.class,
						(com.google.gson.JsonDeserializer<java.time.LocalTime>) (json, typeOfT,
								context) -> java.time.LocalTime.parse(json.getAsString(),
										java.time.format.DateTimeFormatter.ISO_LOCAL_TIME))
				.registerTypeAdapter(java.util.Optional.class,
						(com.google.gson.JsonSerializer<java.util.Optional<?>>) (src, typeOfSrc,
								context) -> src.isPresent() ? context.serialize(src.get())
										: com.google.gson.JsonNull.INSTANCE)
				.create();
	}

}
