package com.project.model;

public class User {
    private int id;
    private String name;
    private String password; // para simplificar
    //private final Historic historic = new Historic();

    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    //public Historic getHistoric() { return historic; }

    public boolean checkPassword(String input) { return password.equals(input); }
}
