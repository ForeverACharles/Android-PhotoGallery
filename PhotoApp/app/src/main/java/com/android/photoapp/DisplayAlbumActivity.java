package com.android.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.*;
import java.util.*;

public class DisplayAlbumActivity extends AppCompatActivity {

    GridView photoGrid;
    BaseAdapter photoAdapter = null;
    FloatingActionButton addPhotoButton;
    static final int WANT_AN_IMAGE = 1;

    ArrayList<Photo> photos;
    static Photo currentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_album);

        Album currentAlbum = PhotoHome.albums.get(PhotoHome.currentAlbum);
        photos = currentAlbum.getPhotos();

        photoGrid = findViewById(R.id.photoGrid);

        displayPhotos();

        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(DisplayAlbumActivity.this, "Opening " + (i + 1) + "th photo in \"" + currentAlbum + "\"", Toast.LENGTH_SHORT).show();
                currentPhoto = photos.get(i);
            }
        });
        registerForContextMenu(photoGrid);

        addPhotoButton = (FloatingActionButton) findViewById(R.id.addPhotoButton);
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
    public void onCreateContextMenu(ContextMenu menu, View menuView, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, menuView, menuInfo);
        if (menuView.getId() == R.id.photoGrid) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.photo_options, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int selectedPhoto= ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;

        switch(item.getItemId()) {

            case R.id.move :

                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayAlbumActivity.this);
                builder.setTitle("Move to Album");
                builder.setMessage("Choose album to move to below");
                ArrayAdapter<Album> adp = new ArrayAdapter<Album>(DisplayAlbumActivity.this,
                        android.R.layout.simple_spinner_item, new ArrayList<Album>());
                for (Album a:PhotoHome.albums){
                    if (PhotoHome.albums.indexOf(a) != PhotoHome.currentAlbum){
                        adp.add(a);
                    }
                }
                // Specify the type of input expected
                final Spinner sp = new Spinner(DisplayAlbumActivity.this);
                sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(adp);
                builder.setView(sp);

                // Set up the buttons
                builder.setPositiveButton("Move", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adp.getItem(sp.getSelectedItemPosition()).getPhotos().add(photos.get(selectedPhoto));
                        photos.remove(selectedPhoto);
                        PhotoHome.saveAppState(DisplayAlbumActivity.this);
                        displayPhotos();
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;

            case R.id.delete :
                photos.remove(selectedPhoto);
                PhotoHome.saveAppState(DisplayAlbumActivity.this);
                displayPhotos();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imgStream = getContentResolver().openInputStream(imageUri);
                final Bitmap testPic = BitmapFactory.decodeStream(imgStream);

                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                testPic.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);

                byte[] bitmapdata = blob.toByteArray();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                PhotoHome.albums.get(PhotoHome.currentAlbum).getPhotos().add(new Photo(bitmapdata));
                photos = PhotoHome.albums.get(PhotoHome.currentAlbum).getPhotos();

                PhotoHome.saveAppState(DisplayAlbumActivity.this);
                displayPhotos();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(DisplayAlbumActivity.this, "Error", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(DisplayAlbumActivity.this, "You didn't pick anything",Toast.LENGTH_LONG).show();
        }
    }

    public void displayPhotos()
    {
        photoAdapter = new PhotoAdapter(DisplayAlbumActivity.this, photos);
        photoGrid.setAdapter(photoAdapter);
    }


}