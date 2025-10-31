package com.project.model;

public class Patient extends User {
    public Patient(int id, String name, String password) {
        super(id, name, password, UserType.PATIENT);
    }

    @Override
    public String getSummary() {
        return "Paciente " + getName() + ": acompanhe suas medições e mantenha seu tratamento em dia.";
    }
}
