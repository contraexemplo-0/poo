package com.project.model;

public class User {
    private int id;
    private String name;
    private String password; // para simplificar
    private final UserType type;
    //private final Historic historic = new Historic();

    protected User(int id, String name, String password, UserType type) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public UserType getType() { return type; }

    //public Historic getHistoric() { return historic; }

    public boolean checkPassword(String input) { return password.equals(input); }

    public String getSummary() {
        return "Usuário: " + name + " (" + type + ")";
    }
}
