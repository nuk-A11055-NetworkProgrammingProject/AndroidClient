package com.example.tcp.ui.gallery;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.tcp.HashPassword;
import com.example.tcp.MainActivity;
import com.example.tcp.R;
import com.example.tcp.SocketConnection;
import com.example.tcp.activity_client;
import com.example.tcp.activity_login;
import com.example.tcp.databinding.FragmentGalleryBinding;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class GalleryFragment extends Fragment {
    private boolean flag = false;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private FragmentGalleryBinding binding;
    private Button add;
    private String username;
    private AlertDialog dialog;
    private LinearLayout layout;

    private TextView AA;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        AA = root.findViewById(R.id.wfw);

        // 建立socket + 取得名字
        Intent intent = requireActivity().getIntent();
        username = intent.getStringExtra("username");
        socket = SocketConnection.get().getSocket();
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        add = root.findViewById(R.id.add);
        layout = root.findViewById(R.id.container);

        // 檢查目前有哪些聊天頻道
        check_now();

        buildDialog();


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        return root;
    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText groupname = view.findViewById(R.id.nameEdit);

        builder.setView(view);
        builder.setTitle("Create Group Name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (bufferedWriter != null) {
                                        String msg = "CREATEGRUP" + username + ":" + groupname.getText().toString();
                                        bufferedWriter.write(msg);
                                        bufferedWriter.newLine();
                                        bufferedWriter.flush();

//                                        String MSG = bufferedReader.readLine();
//                                        AA.setText(MSG.toString());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                        addCard(groupname.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 取消
                    }
                });
        dialog = builder.create();
    }

    private void check_now() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bufferedWriter != null) {
                        String msg = "HAVINGROUP";
                        bufferedWriter.write(msg);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        String MSG = bufferedReader.readLine();

                        String[] tableNames = MSG.split(":");
                        ArrayList<String> having_group = new ArrayList<>();
                        for (String tableName : tableNames) {
                            having_group.add(tableName);
                        }

                        AA.setText(having_group.toString());

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                layout.removeAllViews();
                                first_time_add_card(having_group);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void first_time_add_card(ArrayList<String> name) {
        layout.removeAllViews();
        String len = Integer.toString(name.size());
        Toast.makeText(getActivity(), len, Toast.LENGTH_LONG).show();
        flag = false; // 重置 flag 變數
        for (String nameString : name) {
            if (flag == false) {
                flag = true;
                continue;
            }
            final View view = requireActivity().getLayoutInflater().inflate(R.layout.card, null);
            Button GOGO = view.findViewById(R.id.GOGO);
            final TextView nameView = view.findViewById(R.id.name);

            GOGO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = requireContext();
                    Intent intent = new Intent(context, activity_client.class);
                    intent.putExtra("group_name", nameView.getText().toString());
                    //要接到名字
                    intent.putExtra("username", username.toString());
                    context.startActivity(intent);
                }
            });

            nameView.setText(nameString);
            layout.addView(view);
        }
    }

    private void addCard(String name) {
        final View view = requireActivity().getLayoutInflater().inflate(R.layout.card, null);

        TextView nameView = view.findViewById(R.id.name);

        nameView.setText(name);
        layout.addView(view);

        Button GOGO = view.findViewById(R.id.GOGO);

        GOGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = requireContext();
                Intent intent = new Intent(context, activity_client.class);
                intent.putExtra("group_name", nameView.getText().toString());
                //要接到名字
                intent.putExtra("username", username.toString());
                context.startActivity(intent);
            }
        });
    }
}