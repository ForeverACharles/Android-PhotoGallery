package com.android.photoapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.*;

public class Photo implements Serializable {

    private byte[] photoData;
    private ArrayList<Tag> tags;

    public static final String storeDir = "data";
    public static final String storeFile = "photos.dat";
    static final long serialVersionUID = 1L;

    public Photo(byte[] photoData)
    {
        this.photoData = photoData;
        tags = new ArrayList<Tag>();
    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
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