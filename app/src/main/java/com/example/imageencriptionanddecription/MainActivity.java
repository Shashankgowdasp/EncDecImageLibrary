package com.example.imageencriptionanddecription;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView image_text;
    ImageView image_move_to_next_activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idInitialisations();
        buttonFunctions();
    }
    private void idInitialisations() {
        image_move_to_next_activity=findViewById(R.id.main_move_to_next_Activity);
        image_text=findViewById(R.id.image_text_main);
    }
    private void buttonFunctions() {

        image_move_to_next_activity.setOnClickListener(view -> {
            Intent intent=new Intent(this,ImageCripto.class);
            startActivity(intent);
        });
    }
}