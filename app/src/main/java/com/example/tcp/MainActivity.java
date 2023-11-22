package com.example.tcp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    private Button Join;
    private EditText Username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Join = findViewById(R.id.joinBtn);
        Username = findViewById(R.id.username);

        Join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, client.class);
                intent.putExtra("username", Username.getText().toString());
                startActivity(intent);
            }
        });

    }
}