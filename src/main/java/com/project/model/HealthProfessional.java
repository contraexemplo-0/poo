package com.project.model;

public class HealthProfessional extends User {
    public HealthProfessional(int id, String name, String password) {
        super(id, name, password, UserType.HEALTH_PROFESSIONAL);
    }

    @Override
    public String getSummary() {
        return "Profissional de Saúde " + getName() + ": acompanhe pacientes e suas tendências glicêmicas.";
    }

    @Override
    public String getDashboardSummary() {
        return "Resumo do profissional " + getName() + ": consulte históricos e acompanhe indicadores dos seus pacientes.";
    }
}
