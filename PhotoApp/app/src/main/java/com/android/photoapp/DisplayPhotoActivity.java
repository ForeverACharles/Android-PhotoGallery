package com.android.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
        registerForContextMenu(listView);
        presentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                presentPhoto(view);
            }
        });
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
    public void presentPhoto(View view){
        Intent intent = new Intent(this, SlideshowActivity.class);
        startActivity(intent);
    }
    public void onCreateContextMenu(ContextMenu menu, View menuView, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, menuView, menuInfo);
        if (menuView.getId() == R.id.tagListView) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.tag_options, menu);
        }
    }
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int selectedTag= ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;

        switch(item.getItemId()) {
            case R.id.delete :
                DisplayAlbumActivity.currentPhoto.getTags().remove(selectedTag);
                PhotoHome.saveAppState(DisplayPhotoActivity.this);
                displayTags();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
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
        String[] s = {"Person", "Location"};
        LinearLayout layout = new LinearLayout(DisplayPhotoActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Set up the input
        final EditText input = new EditText(DisplayPhotoActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        final Spinner sp = new Spinner(DisplayPhotoActivity.this);
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(DisplayPhotoActivity.this,
                android.R.layout.simple_spinner_item, s);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adp);
        layout.addView(sp);
        layout.addView(input);
        builder.setView(layout);

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
                else if (getTag((String)sp.getSelectedItem(),name) != null)
                {
                    Toast.makeText(DisplayPhotoActivity.this, "\"" + name + "\" already exists", Toast.LENGTH_LONG).show();
                    addTagDialog(view);
                }
                else
                {
                    DisplayAlbumActivity.currentPhoto.getTags().add(new Tag((String)sp.getSelectedItem(),name));
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