package com.example.tcp.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tcp.HashPassword;
import com.example.tcp.R;
import com.example.tcp.SocketConnection;
import com.example.tcp.databinding.FragmentSlideshowBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SlideshowFragment extends Fragment {
    private EditText a,b;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private TextView update;
    private Button FIX;
    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.example.tcp.ui.slideshow.SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(com.example.tcp.ui.slideshow.SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        a = root.findViewById(R.id.a);
        b = root.findViewById(R.id.b);
        update = root.findViewById(R.id.AA);
        FIX = root.findViewById(R.id.FIX);
        Intent intent = requireActivity().getIntent();
        String username = intent.getStringExtra("username");
//        update.setText(username);

        socket = SocketConnection.get().getSocket();
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FIX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (bufferedWriter != null) {
                                String hash_pwd = HashPassword.hash(b.getText().toString());
                                String msg = "FIXPRIVATE" + username + ":" + hash_pwd;
                                bufferedWriter.write(msg);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                                String AAA = bufferedReader.readLine();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}