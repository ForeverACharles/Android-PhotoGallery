package com.android.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class DisplayAlbumActivity extends AppCompatActivity {
    static InputStream tester = null;
    FloatingActionButton addPhotoButton;
    ImageView imageView;
    static final int WANT_AN_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_album);
        addPhotoButton = (FloatingActionButton) findViewById(R.id.addPhotoButton);
        imageView = findViewById(R.id.imageView2);
        if(!(PhotoHome.albums.get(PhotoHome.currentAlbum).getPhotos().isEmpty())) {
            Bitmap pickedPic = PhotoHome.albums.get(PhotoHome.currentAlbum).getPhotos().get(0).getPhoto();
            imageView.setImageBitmap(pickedPic);
        }
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
    private String getPath(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                //File file = new File(getPath(DisplayAlbumActivity.this, data.getData()));

                //final InputStream imgStream = getContentResolver().openInputStream(Uri.fromFile(new File(imageUri.getPath())));
                //final InputStream imgStream = getContentResolver().openInputStream(Uri.fromFile(new File(getPath(DisplayAlbumActivity.this, data.getData()))));
                //InputStream testStream = new FileInputStream(file);
                final InputStream imgStream = getContentResolver().openInputStream(imageUri);
                final Bitmap testPic = BitmapFactory.decodeStream(imgStream);
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                testPic.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
                byte[] bitmapdata = blob.toByteArray();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                if(bitmap==null){
                    Log.d("Fatalistic Error","didn't work");
                }
                //Toast.makeText(DisplayAlbumActivity.this, "Le Result is: "+  imageUri.compareTo(Uri.fromFile(file)), Toast.LENGTH_LONG).show();

                //final Bitmap pickedPic = BitmapFactory.decodeFile(file.getPath());

                PhotoHome.albums.get(PhotoHome.currentAlbum).getPhotos().add(new Photo(bitmapdata));

                PhotoHome.saveAppState(DisplayAlbumActivity.this);
                //imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(DisplayAlbumActivity.this, "Error", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(DisplayAlbumActivity.this, "You didn't pick anything",Toast.LENGTH_LONG).show();
        }
    }
}