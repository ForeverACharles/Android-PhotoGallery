package com.example.photoapp;

import java.io.Serializable;
import java.util.*;

public class Album implements Serializable {

    private String name;
    private ArrayList<Photo> photos;
    public static final String storeDir = "data";
    public static final String storeFile = "albums.dat";
    static final long serialVersionUID = 1L;

    public Album(String name) {
        this.name = name;
        photos = new ArrayList<Photo>();
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<Photo> getPhotos(){
        return photos;
    }


}

