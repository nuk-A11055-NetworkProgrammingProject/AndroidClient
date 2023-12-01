package com.example.tcp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class activity_login extends AppCompatActivity {
    private EditText Username,Password;
    private Button BTNLOGIN;
    private TextView aa;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private Socket socket;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Username = findViewById(R.id.InputUsername);
        Password = findViewById(R.id.InputPassword);
        BTNLOGIN = findViewById(R.id.BTNLOGIN);
        aa = findViewById(R.id.TEST);
        BTNLOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLogingID = checkUser(Username.getText().toString(),Password.getText().toString());
                System.out.println(isLogingID);
                if(isLogingID){
                    Intent intent = new Intent(activity_login.this,client.class);
                    intent.putExtra("username", Username.getText().toString());
                    startActivity(intent);
                }else {
                    Toast.makeText(activity_login.this,"LOGIN FAIL",Toast.LENGTH_LONG).show();
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("10.0.2.2", 12345);
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    public boolean checkUser(String username,String password){
        final boolean[] check = new boolean[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bufferedWriter != null) {
                        String msg =  "CHECKLOGIN" + username + ":" + password;
                        bufferedWriter.write(msg);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        String response = bufferedReader.readLine();
                        check[0] = Boolean.parseBoolean(response);
                        aa.setText(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return check[0];

    }
}
