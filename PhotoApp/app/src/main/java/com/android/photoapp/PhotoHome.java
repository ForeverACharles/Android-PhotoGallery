package com.android.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.util.*;
import java.io.*;

public class PhotoHome extends AppCompatActivity implements Serializable {

    Context appContext;

    ListView listView;
    Button addAlbumButton;
    TextView empty;
    static ArrayList<Album> albums = new ArrayList<Album>();

    //ArrayList<String> albumNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = PhotoHome.this;

        setContentView(R.layout.photo_home);
        listView = (ListView)findViewById(R.id.listView);
        addAlbumButton = (Button)findViewById(R.id.albumAddButton);
        empty = findViewById(R.id.empty);

        //load up albums from storage
        try
        {
            albums = readAlbums(appContext);
            Log.d("Loading Album","Albums successfully loaded");
        }
        catch (Exception albumsNotFound)
        {
            //create album objects from those if not present as serialized

            Log.d("Loading Album","Albums does not exist");

            if( !saveAppState(appContext) )
            {
                for(Album album : albums)
                {
                    albums.remove(album);
                }
            }
        }

        //display albums in ListView
        displayAlbums();

        //await user interaction with album list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(PhotoHome.this, "Opening \"" +  albums.get(i).toString() + "\"", Toast.LENGTH_SHORT).show();
                openAlbum(view);
            }
        });
        registerForContextMenu(listView);

        //await user interaction with add album button
        addAlbumButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addAlbumDialog(view);
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


    public void addAlbumDialog(View view)
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
                    addAlbumDialog(view);
                }
                else if (getAlbum(name) != null)
                {
                    Toast.makeText(appContext, "\"" + name + "\" already exists", Toast.LENGTH_LONG).show();
                    addAlbumDialog(view);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View menuView, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, menuView, menuInfo);
        if (menuView.getId() == R.id.listView) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);

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

    public static ArrayList<Album> readAlbums(Context appContext) throws IOException, ClassNotFoundException
    {
        FileInputStream albumFile = appContext.openFileInput("albums");
        ObjectInputStream OIS = new ObjectInputStream(albumFile);

        ArrayList<Album> albumList = (ArrayList<Album>)OIS.readObject();
        OIS.close();
        return albumList;
    }

    public static boolean saveAppState(Context appContext)
    {
        try
        {
            writeAlbums(appContext);
            Log.d("Loading Album","Albums successfully created");
            return true;
        }
        catch (Exception albumWriteFailed)
        {
            Log.e("Album Error","Albums failed to be created");
            return false;
        }
    }

    public static void writeAlbums(Context appContext) throws IOException, ClassNotFoundException
    {
        FileOutputStream albumFile = appContext.openFileOutput("albums", Context.MODE_PRIVATE);
        ObjectOutputStream OOS = new ObjectOutputStream(albumFile);

        OOS.writeObject(albums);
        OOS.close();
    }
    public void openAlbum(View view){
        Intent intent = new Intent(this, DisplayAlbumActivity.class);
        startActivity(intent);
    }
}