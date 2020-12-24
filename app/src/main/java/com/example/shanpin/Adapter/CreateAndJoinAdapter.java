package com.example.shanpin.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.shanpin.R;
import com.example.shanpin.bean.MessageBean;
import com.example.shanpin.ui.MessageActivity;
import com.example.shanpin.ui.PostActivity;
import com.example.shanpin.util.AccountUtil;

import java.util.List;

public class CreateAndJoinAdapter extends RecyclerView.Adapter<CreateAndJoinAdapter.ViewHolder>{

    private List<MessageBean> messageList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageName;

        public ViewHolder (View view)
        {
            super(view);
            messageName = (TextView) view.findViewById(R.id.item_message_Name);
        }
    }

    public CreateAndJoinAdapter(List<MessageBean> itemList){
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
                Intent intent=new Intent(view.getContext(), PostActivity.class);
                intent.putExtra("pinID",messageList.get(position).getPinID());
                intent.putExtra("userID", AccountUtil.getAccount(view.getContext()));
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
