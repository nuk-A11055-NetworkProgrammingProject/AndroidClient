package com.example.tcp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class activity_register extends AppCompatActivity {
    private EditText USERNAME,PASSWORD,SAMEPASS;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Button register,login ;
//    private ImageButton login;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        USERNAME = findViewById(R.id.inputUser);
        PASSWORD = findViewById(R.id.inputPass);
        SAMEPASS = findViewById(R.id.inputSamePass);
        register = findViewById(R.id.btnregister);
//        login = findViewById(R.id.btnlogin);


        socket = SocketConnection.get().getSocket();
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user,pwd,samepwd;
                user = USERNAME.getText().toString();
                pwd = PASSWORD.getText().toString();
                samepwd = SAMEPASS.getText().toString();

                if (user.equals("") || pwd.equals("") || samepwd.equals("")) {
                    Toast.makeText(activity_register.this,"請輸入資料",Toast.LENGTH_LONG).show();
                } else {
                    if (pwd.equals(samepwd)) {
                        // 傳送資料給 server ，嘗試註冊
                        String response = tryToRegister(user, pwd);
                        // 顯示 server 回傳的訊息
                        if (response != null) {
                            Toast.makeText(activity_register.this, response, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(activity_register.this,activity_login.class);
                            startActivity(intent);
                        }
                    } else {
                        // 確認密碼 跟密碼不同
                        Toast.makeText(activity_register.this,"輸入密碼不一樣",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity_register.this,activity_login.class);
//                startActivity(intent);
//            }
//        });
    }

    // tryToRegister() 回傳值有三種可能
    // 1. "Username already exist"
    // 2. "Successfully register"
    // 3. "Failed to register" (應該很少發生)
    public String tryToRegister(String username,String password){
        final String[] response = new String[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bufferedWriter != null) {
                        // 傳送 username 與 hash 後的密碼給 server
                        String hash_pwd = HashPassword.hash(password);
                        String msg = "INSERTUSER" + username + ":" + hash_pwd;
                        bufferedWriter.write(msg);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        // 取得 server 回覆
                        response[0] = bufferedReader.readLine();
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

//    public void closeEverything(){
//        try{
//            if(bufferedReader != null){
//                bufferedReader.close();
//            }
//            if(socket != null){
//                socket.close();
//            }
//            if(bufferedWriter != null){
//                bufferedWriter.close();
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//    }

//    public boolean checkUsernameOnServer(String username){
//        final boolean[] check = new boolean[1];
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (bufferedWriter != null) {
//                        String msg = "INSERTUSER" + username;
//                        bufferedWriter.write(msg);
//                        bufferedWriter.newLine();
//                        bufferedWriter.flush();
//
//                        String response = bufferedReader.readLine();
//                        check[0] = Boolean.parseBoolean(response);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return check[0];
//    }
}
