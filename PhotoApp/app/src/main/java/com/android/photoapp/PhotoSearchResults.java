package com.android.photoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class PhotoSearchResults extends AppCompatActivity
{
    GridView photoGrid;
    BaseAdapter photoAdapter = null;
    static final int WANT_AN_IMAGE = 1;

    ArrayList<Photo> photos;
    static Photo currentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_display);

        photos = PhotoHome.searchedPhotos;

        photoGrid = findViewById(R.id.photoSearchGrid);
        displayPhotos();

        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View photoView = view;

                view.setAlpha((float) 0.5);

                currentPhoto = photos.get(i);
                openPhoto(photoView);
            }
        });

    }
    public void displayPhotos()
    {
        photoAdapter = new PhotoAdapter(PhotoSearchResults.this, photos);
        photoGrid.setAdapter(photoAdapter);
    }

    public void openPhoto(View view){
        Intent intent = new Intent(PhotoSearchResults.this, DisplayPhotoActivity.class);
        startActivity(intent);
    }
}
