package com.example.photoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Album> albums;
    //ArrayList<String> albumNames;
    protected void displayStuff(){
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                albums);
        listView.setAdapter(arrayAdapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albums = new ArrayList<Album>();
        albums.add(new Album("first album"));
        albums.add(new Album("second album"));
        albums.add(new Album("third album"));
        //load up albums fro storage
        //create album objects from those if not present as serialized
        //load up album objects into arraylist
        //take string names from arraylist and put it into albumNames
        //display array of albumName

        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);

        displayStuff();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "clicked album: "+i+" "+albums.get(i).toString(), Toast.LENGTH_SHORT).show();
            }
        });
        registerForContextMenu(listView);

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.rename:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Rename Album");

// Set up the input
                final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                        albums.get(i).setName(input.getText().toString());
                        displayStuff();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            case R.id.delete:
                int i = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                albums.remove(i);
                displayStuff();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}