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
import com.example.shanpin.ui.ScoreActivity;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder>{

    private List<MessageBean> messageList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageName;
        ImageView imageView;
        public ViewHolder (View view) {
            super(view);
            messageName = (TextView) view.findViewById(R.id.item_message_Name);
            imageView=view.findViewById(R.id.item_message_icon);
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
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.messageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MessageBean item = messageList.get(position);
                Intent intent;
                if (item.getStatus().equals("0")) {
                    intent = new Intent(view.getContext(), ScoreActivity.class);
                    intent.putExtra("mode", "1");
                    intent.putExtra("pinID", item.getPinID());
                    view.getContext().startActivity(intent);

                } else {
                    intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("pinID", item.getPinID());
                    intent.putExtra("title", item.getTitle());
                    view.getContext().startActivity(intent);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageBean item = messageList.get(position);
        holder.messageName.setText(item.getTitle());
        holder.imageView.setBackgroundResource(R.drawable.msg);
        if(item.getStatus().equals("0")) {
            holder.imageView.setBackgroundResource(R.drawable.get_socre);
            holder.messageName.setText(item.getTitle() +" 信誉评分" +
                    "" );
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
