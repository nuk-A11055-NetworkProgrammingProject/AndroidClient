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
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Username = findViewById(R.id.InputUsername);
        Password = findViewById(R.id.InputPassword);
        BTNLOGIN = findViewById(R.id.BTNLOGIN);
        aa = findViewById(R.id.TEST);

        socket = SocketConnection.get().getSocket();
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BTNLOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 傳送帳密給 server ，嘗試登入
                String response = tryToLogin(Username.getText().toString(),Password.getText().toString());

                if (response != null) {
                    if(response.equals("Login success")){
                        // 登入成功，切換 Activity
                        Intent intent = new Intent(activity_login.this, activity_client.class);
                        intent.putExtra("username", Username.getText().toString());
                        startActivity(intent);
                    } else {
                        // 顯示登入失敗原因
                        Toast.makeText(activity_login.this,response,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    // tryToLogin() 回傳值有三種
    // 1. "Login success"
    // 2. "Wrong password"
    // 3. "Username not found"
    public String tryToLogin(String username,String password){
        final String[] response = new String[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bufferedWriter != null) {
                        // 傳送 username 與 hash 後的密碼給 server
                        String hash_pwd = HashPassword.hash(password);
                        String msg =  "CHECKLOGIN" + username + ":" + hash_pwd;
                        bufferedWriter.write(msg);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        // 取得 server 回覆
                        response[0] = bufferedReader.readLine();
                        aa.setText(response[0]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        // 等待 thread 執行結束再繼續，確保有收到 server 回覆
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response[0];
    }
}
