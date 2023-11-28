package com.example.tcp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class client extends AppCompatActivity {

    private Socket socket;
    private String msgFromGroupChat;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private TextView receivedMessages;
    private EditText editText;
    String username;
    private final List<String> pendingMessages = new ArrayList<>();

    // ... 其他代碼 ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        username = "CHINOBIO";
        receivedMessages = findViewById(R.id.receivedMessages);
        editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(editText.getText().toString());

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("10.0.2.2", 12345);
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    bufferedWriter.write(username);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    sendPendingMessages();
                    listenForMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()){
                    try{
                        msgFromGroupChat = bufferedReader.readLine();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                receivedMessages.append(msgFromGroupChat + "\n");
                            }
                        });
                    }catch (IOException e){
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }
    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
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
    private void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bufferedWriter != null) {
                        bufferedWriter.write(username);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        bufferedWriter.write(username + " :" + message);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }


            }
        }).start();
    }
//receivedMessages.append(editText.getText().toString() + "\n");
    private void sendPendingMessages() {
        for (String message : pendingMessages) {
//            receivedMessages.append("CHINOBIO : " + message + "\n");
            sendMessage(message);
        }
        pendingMessages.clear();
    }

}