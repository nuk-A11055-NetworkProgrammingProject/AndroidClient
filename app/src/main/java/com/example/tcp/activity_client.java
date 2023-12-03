package com.example.tcp;

import android.content.Intent;
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

public class activity_client extends AppCompatActivity {
    private String msgFromGroupChat;
    private TextView receivedMessages;
    private EditText editText;
    private Button button;
    String username;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final List<String> pendingMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        receivedMessages = findViewById(R.id.receivedMessages);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);

        socket = SocketConnection.get().getSocket();
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
//                bufferedWriter.write(username);
//                bufferedWriter.newLine();
//                bufferedWriter.flush();
//                receivedMessages.append("client : " + username  + " is connect" + "\n");
//                sendPendingMessages();
                listenForMessage();
            }
        }).start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(editText.getText().toString());
            }
        });
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
                                receivedMessages.append(msgFromGroupChat  + "\n");
                            }
                        });
                    }catch (IOException e){
//                        closeEverything(socket,bufferedReader,bufferedWriter);
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
                        bufferedWriter.write(username + " : " + message);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                } catch (IOException e) {
//                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }
//receivedMessages.append(editText.getText().toString() + "\n");
    private void sendPendingMessages() {
        for (String message : pendingMessages) {
            receivedMessages.append(username + " : " + message + "\n");
            sendMessage(message);
        }
        pendingMessages.clear();
    }

}