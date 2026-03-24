package com.medisphere.appointment.util;

import com.google.gson.Gson;

public class Utility {
    public static String objectToJson(Object object) {

        return new Gson().toJson(object);
    }
}
