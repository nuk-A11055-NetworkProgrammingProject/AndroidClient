package com.example.tcp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Server extends AppCompatActivity {
    private List<Socket> clients = new ArrayList<>();
    private TextView T;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        T = findViewById(R.id.TE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(12345);
                    T.setText("server is running " + getServAddr() + ":12345 ，wait client connect...");

                    while (true) {
                        Socket socket = serverSocket.accept();
                        clients.add(socket); // Add this line to add the new client to the list of clients
                        InputStream is = socket.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                        BufferedReader br = new BufferedReader(isr);
                        String info = null;
                        while ((info = br.readLine()) != null) {
                            System.out.println("wait client send msg : " + info);
                            broadcastMessage(info); // Add this line to broadcast the message to all clients
                        }
                        socket.shutdownInput();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void broadcastMessage(String message) {
        for (Socket client : clients) {
            try {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"), true);
                out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String getServAddr() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip != null) {
                        if (ip instanceof Inet4Address) {
                            if (ip.getHostAddress().startsWith("172") || ip.getHostAddress().startsWith("20")
                                    || ip.getHostAddress().startsWith("10") || ip.getHostAddress().startsWith("2")) {
                                return ip.getHostAddress();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Error when getting host ip address" + e.getMessage());
        }

        return "";
    }
}