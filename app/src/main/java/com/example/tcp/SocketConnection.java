package com.example.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketConnection {
    private static SocketConnection socketConnection;
    private static Socket socket;

    private SocketConnection() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("10.0.2.2", 12346);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        // 等待 thread 執行結束再繼續
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static SocketConnection get() {
        if (socket == null) {
            socketConnection = new SocketConnection();
        }
        return socketConnection;
    }

    public Socket getSocket() {
        return socket;
    }
}
