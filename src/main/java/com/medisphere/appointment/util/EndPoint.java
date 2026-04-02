package com.medisphere.appointment.util;

public class EndPoint {
    public static final String DOCTORS_BY_SPECIALITY = "appointments/doctorsBySpeciality/{speciality}";
    public static final String BOOK_APPOINTMENT = "appointments/bookAppointment";
    public static final String UPDATE_APPOINTMENT = "appointments/updateAppointment";
    public static final String CANCEL_APPOINTMENT = "appointments/cancel/{appointmentReferenceId}";
    public static final String TRACK_APPOINTMENT_STATUS = "appointments/trackStatus/{appointmentReferenceId}";

}
