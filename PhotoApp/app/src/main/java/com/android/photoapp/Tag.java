package com.android.photoapp;

import java.io.Serializable;

public class Tag implements Serializable
{
    private String tag;
    private String value;

    public static final String storeDir = "data";
    public static final String storeFile = "tags.dat";
    static final long serialVersionUID = 1L;

    public Tag(String tag, String value)
    {
        this.tag = tag;
        this.value = value;
    }

    public String getTag()
    {
        return tag;
    }

    public String getValue()
    {
        return value;
    }

    public String toString()
    {
        return tag + ": " + value;
    }
}