package com.example.bestlocationapp.model;

public class Position {
    private int id;
    private String pseudo,longitude,latitude,numero;

    public Position(int id, String pseudo, String longitude, String latitude, String numero) {
        this.id = id;
        this.pseudo = pseudo;
        this.longitude = longitude;
        this.latitude = latitude;
        this.numero = numero;
    }


    public Position(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
