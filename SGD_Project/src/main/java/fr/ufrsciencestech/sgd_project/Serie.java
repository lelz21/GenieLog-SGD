/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ufrsciencestech.sgd_project;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author ab750519
 */
public class Serie 
{
    private Date dateS;
    private String[] editeur;
    private String description;
    private ArrayList<Jeu> jeux;
    
    public Serie()
    {
        this.dateS = null;
        this.editeur = null;
        this.description = "";
        this.jeux = null;
    }

    public Serie(Date dateS, String[] editeur, String description, ArrayList<Jeu> jeux)
    {
        this.dateS = dateS;
        this.editeur = editeur;
        this.description = description;
        this.jeux = jeux;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Jeu> getJeux() {
        return jeux;
    }

    public void setJeux(ArrayList<Jeu> jeux) {
        this.jeux = jeux;
    }
    
}