package com.project.model;

public abstract class User {
    private int id;
    private String name;
    private String password; // para simplificar

    protected User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSummary() {
        return "Usuário: " + name;
    }

    public String getDashboardSummary() {
        return "Bem-vindo, " + name + ".";
    }
}
