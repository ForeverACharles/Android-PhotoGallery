package com.android.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DisplayPhotoActivity extends AppCompatActivity {

    FloatingActionButton addTagButton;
    Button presentButton;
    ImageView imageView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        listView = (ListView)findViewById(R.id.tagListView);
        presentButton = (Button)findViewById(R.id.PresentButton);
        imageView = (ImageView)findViewById(R.id.imageView);
        addTagButton = (FloatingActionButton)findViewById(R.id.addTagButton);
        imageView.setImageBitmap(DisplayAlbumActivity.currentPhoto.getBitmap());
        displayTags();
        //registerForContextMenu(listView);

        //await user interaction with add album button
        addTagButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addTagDialog(view);
            }
        });

    }
    protected void displayTags() {
        if(!DisplayAlbumActivity.currentPhoto.getTags().isEmpty()) {
            ArrayAdapter<Tag> arrayAdapter = new ArrayAdapter(
                    DisplayPhotoActivity.this,
                    android.R.layout.simple_list_item_1,
                    DisplayAlbumActivity.currentPhoto.getTags());
            listView.setAdapter(arrayAdapter);
        }
    }
    public static Tag getTag(String tag, String value)
    {
        for(Tag t : DisplayAlbumActivity.currentPhoto.getTags())
        {
            if(t.getTag().equals(tag)&&t.getValue().equals(value))
            {
                return t;
            }
        }
        return null;
    }

    public void addTagDialog(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayPhotoActivity.this);
        builder.setTitle("Add Tag");
        builder.setMessage("Select tag attributes below");

        // Set up the input
        final EditText input = new EditText(DisplayPhotoActivity.this);
        input.setPadding(25,0,25,15);

        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name = input.getText().toString();
                if(name.equals(""))
                {
                    Toast.makeText(DisplayPhotoActivity.this, "Tag name cannot be empty", Toast.LENGTH_LONG).show();
                    addTagDialog(view);
                }
                else if (getTag("Person",name) != null)
                {
                    Toast.makeText(DisplayPhotoActivity.this, "\"" + name + "\" already exists", Toast.LENGTH_LONG).show();
                    addTagDialog(view);
                }
                else
                {
                    DisplayAlbumActivity.currentPhoto.getTags().add(new Tag("Person",name));
                    PhotoHome.saveAppState(DisplayPhotoActivity.this);
                    displayTags();
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}