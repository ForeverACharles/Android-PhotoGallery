package com.android.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DisplayAlbumActivity extends AppCompatActivity {
    InputStream tester = null;
    FloatingActionButton addPhotoButton;
    ImageView imageView;
    static final int WANT_AN_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_album);
        addPhotoButton = (FloatingActionButton) findViewById(R.id.addPhotoButton);
        imageView = findViewById(R.id.imageView2);
        addPhotoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent pIntent = new Intent(Intent.ACTION_PICK);
                pIntent.setType("image/*");
                startActivityForResult(pIntent, WANT_AN_IMAGE);
            }
        });
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                File file = new File(imageUri.getLastPathSegment());
                Toast.makeText(DisplayAlbumActivity.this, "edited" +imageUri.getPath(), Toast.LENGTH_LONG).show();
                final InputStream imgStream = getContentResolver().openInputStream(imageUri);
                if(tester == null) {
                    tester = imgStream;
                }
                final Bitmap pickedPic = BitmapFactory.decodeStream(tester);
                imageView.setImageBitmap(pickedPic);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(DisplayAlbumActivity.this, "Error", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(DisplayAlbumActivity.this, "You didn't pick anything",Toast.LENGTH_LONG).show();
        }
    }
}