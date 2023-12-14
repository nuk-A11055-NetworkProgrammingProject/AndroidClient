package com.example.tcp.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcp.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<MsgItem> msgItems;

    public MyAdapter(Context context, ArrayList<MsgItem> msgItems) {
        this.context = context;
        this.msgItems = msgItems;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.msg_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        holder.msgName.setText(msgItems.get(position).getMsgName());
        holder.msgContent.setText(msgItems.get(position).getMsgContent());
        holder.msgTime.setText(msgItems.get(position).getMsgTime());
    }

    @Override
    public int getItemCount() {
        return this.msgItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView msgName, msgContent, msgTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            msgName = itemView.findViewById(R.id.msgName);
            msgContent = itemView.findViewById(R.id.msgContent);
            msgTime = itemView.findViewById(R.id.msgTime);
        }
    }
}
