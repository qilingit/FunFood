package com.example.funfood;

public class Restaurant {

    private String nom;
    private String adresse;
    private int image;

    public Restaurant(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
        this.image = R.id.photo_resto;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }


    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
