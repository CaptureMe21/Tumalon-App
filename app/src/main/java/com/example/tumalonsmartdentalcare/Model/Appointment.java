package com.example.tumalonsmartdentalcare.Model;

public class Appointment {
    private String appointmentId;
    private String serviceId;
    private String scheduleDate;
    private String scheduleTime;
    private long createdAt;
    private boolean appointmentStatus;

    // Default constructor for Firebase
    public Appointment() {}

    public Appointment(String appointmentId, String serviceId, String scheduleDate, String scheduleTime, long createdAt, boolean appointmentStatus) {
        this.appointmentId = appointmentId;
        this.serviceId = serviceId;
        this.scheduleDate = scheduleDate;
        this.scheduleTime = scheduleTime;
        this.createdAt = createdAt;
        this.appointmentStatus = appointmentStatus;
    }

    // Getters and Setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(String scheduleDate) { this.scheduleDate = scheduleDate; }

    public String getScheduleTime() { return scheduleTime; }
    public void setScheduleTime(String scheduleTime) { this.scheduleTime = scheduleTime; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isAppointmentStatus() { return appointmentStatus; }
    public void setAppointmentStatus(boolean appointmentStatus) { this.appointmentStatus = appointmentStatus; }
}

