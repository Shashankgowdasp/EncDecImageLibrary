package com.example.imageencriptionanddecription;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageencriptionanddecription.Model.Image;

import java.util.ArrayList;

public class MainActivityNew extends AppCompatActivity {

    private static final String AES_KEY = "YourSecretKey123";
    ImageView imageView;
    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ArrayList<Image> galleryItems;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        recyclerView = findViewById(R.id.home_recyclerview);
        databaseHelper = new DatabaseHelper(this);

        recyclerview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecyclerView();
    }

    private void recyclerview() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Set the number of columns as desired
        galleryItems = new ArrayList<>(); //to  Initialize the list of gallery items
        adapter = new GalleryAdapter(this, galleryItems);
        recyclerView.setAdapter(adapter);
    }


    private void loadRecyclerView() {
        galleryItems = databaseHelper.getImages();
        adapter.setItems(galleryItems);
    }

}
