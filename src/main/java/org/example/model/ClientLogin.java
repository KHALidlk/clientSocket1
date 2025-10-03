package org.example.model;

public class ClientLogin implements java.io.Serializable{
    private String username;

    public ClientLogin(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
