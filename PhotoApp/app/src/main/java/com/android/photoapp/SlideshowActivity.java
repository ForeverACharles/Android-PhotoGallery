package com.android.photoapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SlideshowActivity extends AppCompatActivity {

    Button backButton;
    Button forwardButton;
    ImageView imageView;
    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        setTitle("Slideshow");
        backButton = (Button)findViewById(R.id.back);
        forwardButton = (Button)findViewById(R.id.forward);
        imageView = (ImageView)findViewById(R.id.slideView);
        imageView.setImageBitmap(DisplayAlbumActivity.currentPhoto.getBitmap());
        i = PhotoHome.currentAlbum.getPhotos().indexOf(DisplayAlbumActivity.currentPhoto);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = Math.max(0,i-1);
                imageView.setImageBitmap(PhotoHome.currentAlbum.getPhotos().get(i).getBitmap());
            }
        });
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = Math.min(PhotoHome.currentAlbum.getPhotos().size()-1, i+1);
                imageView.setImageBitmap(PhotoHome.currentAlbum.getPhotos().get(i).getBitmap());
            }
        });
    }
}