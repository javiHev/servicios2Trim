package org.example.correoelectronico;

public class Usuario {
    private String userName;
    private String password;

    public Usuario(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    // Getters
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setContraseña(String nuevaContraseña) {
    }
}
