package com.android.photoapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class Photo implements Serializable {

    private String path;
    private byte[] pic;
    private ArrayList<Tag> tags;

    public static final String storeDir = "data";

    public static final String storeFile = "photos.dat";

    static final long serialVersionUID = 1L;

    public Photo(byte[] photo)
    {
        //this.path = path;
        this.pic = photo;
        tags = new ArrayList<Tag>();
    }

    public Bitmap getPhoto() {
        final Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
        return bitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getCaption()
    {
        return path;
    }

    public ArrayList<Tag> getTags()
    {
        return tags;
    }

    public void addTag(Tag newTag)
    {
        tags.add(newTag);
    }

    public void deleteTag(Tag tag)
    {
        tags.remove(tag);
    }

    public boolean containsTag(Tag stag) {
        for (Tag tag: tags) {
            if (tag.getTag().compareTo(stag.getTag())==0 && tag.getValue().compareTo(stag.getValue())==0) {
                return true;
            }
        }
        return false;
    }

}