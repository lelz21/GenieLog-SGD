/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ufrsciencestech.sgd_project;

import java.util.Date;

/**
 *
 * @author ab750519
 */
public class Jeu
{
    private String titre;
    private Date dateS;
    private String[] editeur;
    private double prix;
    private String serie;
    private String[] extension;
    private String description;
    private String Pegi;
    private int nbJouers;
    private String[] plateformes;
    private String[] type;
    private int stock;

    public Jeu() 
    {
        this.titre = "";
        this.dateS = null;
        this.editeur = null;
        this.prix = 0.0;
        this.serie = "";
        this.extension = null;
        this.description = "";
        this.Pegi = "";
        this.nbJouers = 0;
        this.plateformes = null;
        this.type = null;
        this.stock = 0;
    }
    public Jeu(String titre, Date dateS, String[] editeur, double prix, String serie, String[] extension, String description, String Pegi, int nbJouers, String[] plateformes, String[] type, int stock) {
        this.titre = titre;
        this.dateS = dateS;
        this.editeur = editeur;
        this.prix = prix;
        this.serie = serie;
        this.extension = extension;
        this.description = description;
        this.Pegi = Pegi;
        this.nbJouers = nbJouers;
        this.plateformes = plateformes;
        this.type = type;
        this.stock = stock;
    }
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Date getDateS() {
        return dateS;
    }

    public void setDateS(Date dateS) {
        this.dateS = dateS;
    }

    public String[] getEditeur() {
        return editeur;
    }

    public void setEditeur(String[] editeur) {
        this.editeur = editeur;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String[] getExtension() {
        return extension;
    }

    public void setExtension(String[] extension) {
        this.extension = extension;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPegi() {
        return Pegi;
    }

    public void setPegi(String Pegi) {
        this.Pegi = Pegi;
    }

    public int getNbJouers() {
        return nbJouers;
    }

    public void setNbJouers(int nbJouers) {
        this.nbJouers = nbJouers;
    }

    public String[] getPlateformes() {
        return plateformes;
    }

    public void setPlateformes(String[] plateformes) {
        this.plateformes = plateformes;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    
}
