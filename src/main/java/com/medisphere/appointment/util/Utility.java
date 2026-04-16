package com.medisphere.appointment.util;

import com.google.gson.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Utility {
        private static final Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDate.class,
                                        (JsonSerializer<LocalDate>) (src, typeOfSrc,
                                                        context) -> new JsonPrimitive(
                                                                        src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                        .registerTypeAdapter(LocalDate.class,
                                        (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> LocalDate.parse(
                                                        json.getAsString(),
                                                        DateTimeFormatter.ISO_LOCAL_DATE))
                        .registerTypeAdapter(LocalTime.class,
                                        (JsonSerializer<LocalTime>) (src, typeOfSrc,
                                                        context) -> new JsonPrimitive(
                                                                        src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
                        .registerTypeAdapter(LocalTime.class,
                                        (JsonDeserializer<LocalTime>) (json, typeOfT, context) -> LocalTime.parse(
                                                        json.getAsString(),
                                                        DateTimeFormatter.ISO_LOCAL_TIME))
                        .registerTypeAdapter(java.time.LocalDateTime.class,
                                        (JsonSerializer<java.time.LocalDateTime>) (src, typeOfSrc,
                                                        context) -> new JsonPrimitive(src
                                                                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                        .registerTypeAdapter(java.time.LocalDateTime.class,
                                        (JsonDeserializer<java.time.LocalDateTime>) (json, typeOfT,
                                                        context) -> java.time.LocalDateTime.parse(json.getAsString(),
                                                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .registerTypeAdapter(Optional.class,
                                        (JsonSerializer<Optional<?>>) (src, typeOfSrc,
                                                        context) -> src.isPresent() ? context.serialize(src.get())
                                                                        : JsonNull.INSTANCE)
                        .create();

        public static String objectToJson(Object object) {
                return gson.toJson(object);
        }
}
