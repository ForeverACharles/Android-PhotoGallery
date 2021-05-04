package com.android.photoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.*;


public class PhotoAdapter extends BaseAdapter {

    private Context context;
    ArrayList<Bitmap> photoBitmaps = new ArrayList<Bitmap>();

    public PhotoAdapter(Context context, ArrayList<Photo> photos)
    {
        this.context = context;
        setBitmaps(photos);
    }

    public void setBitmaps(ArrayList<Photo> photos)
    {
        for(Photo photo : photos)
        {
            photoBitmaps.add(photo.getBitmap());
        }
    }

    public int getCount()
    {
        return photoBitmaps.size();
    }

    public Object getItem(int index)
    {
        return null;
    }

    public long getItemId(int index)
    {
        return 0;
    }

    public View getView(int index, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if(convertView == null)
        {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5,5,5,0);
            imageView.setImageAlpha(255);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        if(photoBitmaps.isEmpty())
        {
            return null;
        }
        imageView.setImageBitmap(photoBitmaps.get(index));
        return imageView;
    }
}
