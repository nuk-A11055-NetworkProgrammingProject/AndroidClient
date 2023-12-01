package com.example.tcp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class activity_register extends AppCompatActivity {
    private EditText USERNAME,PASSWORD,SAMEPASS;

    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private Socket socket;
    private final List<String> pendingMessages = new ArrayList<>();

    private Button register,login;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        USERNAME = findViewById(R.id.inputUser);
        PASSWORD = findViewById(R.id.inputPass);
        SAMEPASS = findViewById(R.id.inputSamePass);
        register = findViewById(R.id.btnregister);
        login = findViewById(R.id.btnlogin);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user,pwd,samepwd;
                user = USERNAME.getText().toString();
                pwd = PASSWORD.getText().toString();
                samepwd = SAMEPASS.getText().toString();
                if(user.equals("") || pwd.equals("") || samepwd.equals("")){
                    Toast.makeText(activity_register.this,"請輸入資料",Toast.LENGTH_LONG).show();
                }else {
                    if(pwd.equals(samepwd)){
                        boolean check_exist_user = checkUsernameOnServer(user);
                        // 確認帳號存不存在
                        if(!check_exist_user){
                            Toast.makeText(activity_register.this,"user exist",Toast.LENGTH_LONG).show();
                            return;
                        }
                        boolean register_Success = insertDataOnServer(user,pwd);
                        if(register_Success){
                            Toast.makeText(activity_register.this,"register success",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(activity_register.this,"register fail",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        // 確認密碼 跟密碼不同
                        Toast.makeText(activity_register.this,"輸入密碼不一樣",Toast.LENGTH_LONG).show();
                    }
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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_register.this,activity_login.class);
                startActivity(intent);
            }
        });
    }

    public void closeEverything(){
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(socket != null){
                socket.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public boolean insertDataOnServer(String username,String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bufferedWriter != null) {
                        String msg =  username + ":" + password;
                        bufferedWriter.write(msg);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }
    public boolean checkUsernameOnServer(String username){
        final boolean[] check = new boolean[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bufferedWriter != null) {
                        String msg = "INSERTUSER" + username;
                        bufferedWriter.write(msg);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        String response = bufferedReader.readLine();
                        check[0] = Boolean.parseBoolean(response);
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
