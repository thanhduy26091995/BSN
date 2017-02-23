package com.hbbmobile.bsnapp.home_page.model;

/**
 * Created by buivu on 11/11/2016.
 */
public class HomePage {
    private String typeFramework;
    private String categorie;
    private String timeLimit;
    private String price;
    private String description;
    private String projectName;

    public HomePage() {
    }

    public HomePage(String typeFramework, String categorie, String timeLimit, String price, String description, String projectName) {
        this.typeFramework = typeFramework;
        this.categorie = categorie;
        this.timeLimit = timeLimit;
        this.price = price;
        this.description = description;
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTypeFramework() {
        return typeFramework;
    }

    public void setTypeFramework(String typeFramework) {
        this.typeFramework = typeFramework;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
