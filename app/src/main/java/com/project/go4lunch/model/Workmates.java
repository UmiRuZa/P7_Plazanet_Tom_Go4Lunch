package com.project.go4lunch.model;

public class Workmates {

    private String wmLunch;
    private String wmPpURL;

    public Workmates() {

    }

    public Workmates(String wmLunch, String wmPpURL) {
        this.wmLunch = wmLunch;
        this.wmPpURL = wmPpURL;
    }

    public String getWmLunch() {
        return wmLunch;
    }

    public void setWmLunch(String wmLunch) {
        this.wmLunch = wmLunch;
    }

    public String getWmPpURL() {
        return wmPpURL;
    }

    public void setWmPpURL(String wmPpURL) {
        this.wmPpURL = wmPpURL;
    }
}
