package com.example.pop.model;

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String currentFolder;

    public User(){

    }

    public User(int id,String firstName, String lastName, String email, String currentFolder){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.currentFolder = currentFolder;
    }

    public User(String firstName, String lastName, String email, String password, String currentFolder){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.currentFolder = currentFolder;
    }
    public User(int id, String firstName, String lastName, String email, String password, String currentFolder){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.currentFolder = currentFolder;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    public String getCurrentFolder() {
        return currentFolder;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setCurrentFolder(String currentFolder) {
        this.currentFolder = currentFolder;
    }

}
