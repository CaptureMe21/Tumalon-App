package com.example.tumalonsmartdentalcare.Model;

public class Service {
    private String serviceId;
    private String serviceName;
    private String serviceFee;
    private boolean isSelected; // To track if the checkbox is selected

    public Service(String serviceId, String serviceName, String serviceFee) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceFee = serviceFee;
        this.isSelected = false; // Default state
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceFee() {
        return serviceFee;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
