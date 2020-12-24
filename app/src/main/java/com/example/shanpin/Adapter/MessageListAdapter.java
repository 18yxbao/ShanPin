package com.example.shanpin.Adapter;

import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shanpin.R;
import com.example.shanpin.bean.MessageBean;
import com.example.shanpin.bean.PinBean;
import com.example.shanpin.ui.MessageActivity;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder>{

    private List<MessageBean> messageList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageName;

        public ViewHolder (View view)
        {
            super(view);
            messageName = (TextView) view.findViewById(R.id.item_message_Name);
        }
    }

    public MessageListAdapter(List<MessageBean> itemList){
        this.messageList = itemList;
    }

    public void setMessageList(List<MessageBean> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.messageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position =holder.getAdapterPosition();
                Intent intent=new Intent(view.getContext(),MessageActivity.class);
                intent.putExtra("pinID",messageList.get(position).getPinID());
                intent.putExtra("title",messageList.get(position).getTitle());
                view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageBean item = this.messageList.get(position);
        holder.messageName.setText(item.getTitle());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
