package com.example.tcp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcp.message.MsgItem;
import com.example.tcp.message.MyAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class activity_client extends AppCompatActivity {
    private String[] splitReceivedMsg;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private ArrayList<MsgItem> msgArray;
    private EditText editText;
    private FrameLayout layoutSend;
    String username;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        editText = findViewById(R.id.editText);
        layoutSend = (android.widget.FrameLayout) findViewById(R.id.layoutSend);
        recyclerView = findViewById(R.id.recyclerView);


        msgArray = new ArrayList<>();
        myAdapter = new MyAdapter(activity_client.this, msgArray);

        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity_client.this));

        // 取得群組名稱
        String groupName = intent.getStringExtra("group_name");

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
                try {
                    // 通知 server 用戶已進入群組聊天及進入的群組名稱
                    bufferedWriter.write("ENTERGROUP" + groupName);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } catch (IOException e) {

                }

                // 讀完歷史訊息再接收廣播訊息
                readHistoryMessage();
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

    public void  readHistoryMessage() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 接收歷史訊息
                while (socket.isConnected()) {
                    try {
                        String msgName = bufferedReader.readLine();
                        String msgContent = bufferedReader.readLine();
                        String msgTime = bufferedReader.readLine();

                        // 若收到歷史訊息結束的 signal 就跳出迴圈
                        if (msgName.equals("end") && msgContent.equals("end")
                                && msgTime.equals("end")) {
                            break;
                        }

                        // 加入訊息至msgArray
                        msgArray.add(new MsgItem(msgName, msgContent, msgTime));
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        // 接收伺服器broadcast的訊息
                        String msgName = bufferedReader.readLine();
                        String msgContent = bufferedReader.readLine();
                        String msgTime = bufferedReader.readLine();

                        // 加入訊息至msgArray
                        msgArray.add(new MsgItem(msgName, msgContent, msgTime));
                        runOnUiThread(new Runnable() {  // 刷新聊天介面
                            @Override
                            public void run() {
                                // 刷新聊天訊息
                                myAdapter.notifyItemInserted(msgArray.size() - 1);
                                // 訊息超出螢幕時，自動往下滾動
                                recyclerView.scrollToPosition(msgArray.size() - 1);
                            }
                        });
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bufferedWriter != null) {
                        bufferedWriter.write(username + "\n");
                        bufferedWriter.write(message + "\n");

                        // 取得目前時間
                        Date currentDate = new Date();
                        String timestamp = new Timestamp(currentDate.getTime()).toString();
                        bufferedWriter.write(timestamp + "\n");
                        bufferedWriter.flush();
                    }
                } catch (IOException e) {
//                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }
}