package com.example.tcp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
    private ScrollView scrollView;
    private TextView receivedMessages;
    private TextView TEST;
    private EditText editText;
    private FrameLayout layoutSend;
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
        editText = findViewById(R.id.editText);
        layoutSend = (android.widget.FrameLayout) findViewById(R.id.layoutSend);

        String groupName = intent.getStringExtra("group_name");
//        TEST.setText(groupName);
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
                listenForMessage();
            }
        }).start();

        layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(editText.getText().toString());
                editText.getText().clear();
            }
        });
    }

    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
        class ViewHolder extends RecyclerView.ViewHolder{
            TextView msgName, msgContent, msgTime;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                msgName = itemView.findViewById(R.id.msgName);
                msgContent = itemView.findViewById(R.id.msgContent);
                msgTime = itemView.findViewById(R.id.msgTime);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
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
//                                receivedMessages.append(msgFromGroupChat  + "\n");
//                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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


}