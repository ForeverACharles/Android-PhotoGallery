package com.android.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;
import java.io.*;

public class PhotoHome extends AppCompatActivity implements Serializable {

    public static Context appContext;

    ListView listView;
    Button addAlbumButton;
    FloatingActionButton photoSearchButton;
    TextView empty;
    public static Album currentAlbum;
    public static ArrayList<Album> albums = new ArrayList<Album>();
    public static Album searchedPhotos;
    public static boolean Search = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = PhotoHome.this;
        setContentView(R.layout.photo_home);
        listView = (ListView) findViewById(R.id.listView);
        addAlbumButton = (Button) findViewById(R.id.albumAddButton);
        photoSearchButton = (FloatingActionButton) findViewById(R.id.photoSearchButton);
        empty = findViewById(R.id.empty);

        //load up albums from storage
        try {
            albums = readAlbums(appContext);
            Log.d("Loading Album", "Albums successfully loaded");
        } catch (Exception albumsNotFound) {
            //create album objects from those if not present as serialized

            Log.d("Loading Album", "Albums does not exist");
            saveAppState(appContext);
        }

        //display albums in ListView
        displayAlbums();

        //await user interaction with album list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(PhotoHome.this, "Opening \"" + albums.get(i).toString() + "\"", Toast.LENGTH_SHORT).show();
                currentAlbum = albums.get(i);
                Search = false;
                openAlbum();
            }
        });
        registerForContextMenu(listView);

        //await user interaction with add album button
        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAlbumDialog();
            }
        });

        //search for Photo
        photoSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPhotoDialog(view);
            }
        });
    }
    protected void displayAlbums() {
        if(albums.size() == 0)
        {
            empty.setText("Looks like you have no albums");
        }
        else
        {
            empty.setText(null);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                albums);
        listView.setAdapter(arrayAdapter);
    }

    public static Album getAlbum(String albumName)
    {
        for(Album album : albums)
        {
            if(album.toString().equals(albumName))
            {
                return album;
            }
        }
        return null;
    }

    public static void sortAlbums()
    {
        Collections.sort(albums, new Comparator<Album>() {
            @Override
            public int compare(Album thisAlbum, Album thatAlbum) {
                return thisAlbum.toString().compareTo(thatAlbum.toString());
            }
        });
    }

    public void addAlbumDialog()
    {
        appContext = PhotoHome.this;
        AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
        builder.setTitle("Create Album");
        builder.setMessage("Enter album name below");

        // Set up the input
        final EditText input = new EditText(appContext);
        input.setPadding(25,0,25,15);

        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name = input.getText().toString();
                if(name.equals(""))
                {
                    Toast.makeText(appContext, "Album name cannot be empty", Toast.LENGTH_LONG).show();
                    addAlbumDialog();
                }
                else if (getAlbum(name) != null)
                {
                    Toast.makeText(appContext, "\"" + name + "\" already exists", Toast.LENGTH_LONG).show();
                    addAlbumDialog();
                }
                else
                {
                    albums.add(new Album(name));
                    sortAlbums();
                    saveAppState(appContext);
                    displayAlbums();
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

    public void searchPhotoDialog(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoHome.this);
        builder.setTitle("Search Photos");
        builder.setMessage("Enter your search below");
        String[] s = {"Person", "Location"};
        LinearLayout layout = new LinearLayout(PhotoHome.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Set up the input
        final EditText input = new EditText(appContext);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        final Spinner sp = new Spinner(PhotoHome.this);
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(PhotoHome.this,
                android.R.layout.simple_spinner_item, s);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adp);


        layout.addView(sp);
        layout.addView(input);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String search = input.getText().toString();
                if(search.equals(""))
                {
                    Toast.makeText(appContext, "Search was empty", Toast.LENGTH_LONG).show();
                    searchPhotoDialog(view);
                }
                else if((searchedPhotos = searchPhotos((String)sp.getSelectedItem(),search)).getPhotos().isEmpty())
                {
                    Toast.makeText(appContext, "No results for \"" + search + "\"", Toast.LENGTH_LONG).show();
                }
                else
                {
                    //go to next view
                    Toast.makeText(appContext, "Showing results for \"" + search + "\"" , Toast.LENGTH_LONG).show();
                    currentAlbum = searchedPhotos;
                    Search = true;
                    openAlbum();
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

    public Album searchPhotos(String type, String search)
    {
        Album searchResults = new Album("Search Results");
        for(Album album : albums)
        {
            for(Photo photo : album.getPhotos())
            {
                for(Tag tag : photo.getTags())
                {
                    if(type.equals(tag.getTag())&&tag.getValue().toLowerCase().startsWith(search.toLowerCase()))
                        {
                            searchResults.getPhotos().add(photo);
                        }
                }
            }
        }
        return searchResults;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View menuView, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, menuView, menuInfo);
        if (menuView.getId() == R.id.listView) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.album_options, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int selectedAlbum = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;

        switch(item.getItemId()) {

            case R.id.rename :

                appContext = this;
                AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
                builder.setTitle("Album Rename");
                builder.setMessage("Rename \"" + albums.get(selectedAlbum).toString() + "\" below");

                // Set up the input
                final EditText input = new EditText(appContext);
                input.setPadding(25,0,25,15);

                // Specify the type of input expected
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("RENAME", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String rename = input.getText().toString();
                        if(rename.equals(""))
                        {
                            Toast.makeText(appContext, "Album name cannot be empty", Toast.LENGTH_LONG).show();
                            onContextItemSelected(item);
                        }
                        else if (getAlbum(rename) != null)
                        {
                            Toast.makeText(appContext, "\"" + rename + "\" already exists", Toast.LENGTH_LONG).show();
                            onContextItemSelected(item);
                        }
                        else
                        {
                            albums.get(selectedAlbum).setName(rename);
                            saveAppState(appContext);
                            displayAlbums();
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
                return true;

            case R.id.delete :
                albums.remove(selectedAlbum);
                saveAppState(appContext);
                displayAlbums();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public static ArrayList<Album> readAlbums(Context context) throws IOException, ClassNotFoundException
    {
        FileInputStream albumFile = context.openFileInput("albums");
        ObjectInputStream OIS = new ObjectInputStream(albumFile);

        ArrayList<Album> albumList = (ArrayList<Album>)OIS.readObject();
        OIS.close();
        return albumList;
    }

    public static boolean saveAppState(Context context)
    {
        try
        {
            writeAlbums(context);
            Log.d("Loading Album","Albums successfully created");
            return true;
        }
        catch (Exception albumWriteFailed)
        {
            Log.e("Album Error","Albums failed to be created");
            return false;
        }
    }

    public static void writeAlbums(Context context) throws IOException, ClassNotFoundException
    {
        FileOutputStream albumFile = context.openFileOutput("albums", Context.MODE_PRIVATE);
        ObjectOutputStream OOS = new ObjectOutputStream(albumFile);

        OOS.writeObject(albums);
        OOS.close();
    }
    public void openAlbum(){
        Intent intent = new Intent(this, DisplayAlbumActivity.class);
        startActivity(intent);
    }

}