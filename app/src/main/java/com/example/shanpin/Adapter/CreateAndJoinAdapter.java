package com.example.shanpin.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.shanpin.R;
import com.example.shanpin.bean.MessageBean;
import com.example.shanpin.ui.MessageActivity;
import com.example.shanpin.ui.PostActivity;
import com.example.shanpin.util.AccountUtil;

import java.util.ArrayList;
import java.util.List;

public class CreateAndJoinAdapter extends RecyclerView.Adapter<CreateAndJoinAdapter.ViewHolder>{

    private List<MessageBean> messageList;
    private int manage=0;
    private List<MessageBean> manageList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageName;
        ImageView imageView;
        public ViewHolder (View view)
        {
            super(view);
            messageName = (TextView) view.findViewById(R.id.item_message_Name);
            imageView=view.findViewById(R.id.item_message_is_choose);
        }
    }

    public CreateAndJoinAdapter(List<MessageBean> itemList){
        this.messageList = itemList;
        this.manageList=new ArrayList<>();
    }

    public void setMessageList(List<MessageBean> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    public void setManage(int manage){
        this.manage=manage;
        manageList.clear();
        notifyDataSetChanged();
    }

    public List<MessageBean>  getManageList() {
        return manageList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
        final ViewHolder holder = new ViewHolder(view);
            holder.messageName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if(manage==0) {
                        Intent intent = new Intent(view.getContext(), PostActivity.class);
                        intent.putExtra("pinID", messageList.get(position).getPinID());
                        intent.putExtra("userID", AccountUtil.getAccount(view.getContext()));
                        view.getContext().startActivity(intent);
                    }else{
                        int pos=-1;
                        pos=manageList.indexOf(messageList.get(position));
                        if((pos)==-1) {
                            manageList.add(messageList.get(position));
                            holder.imageView.setBackgroundResource(R.drawable.checkbox_on_background);
                        }else{
                            manageList.remove(pos);
                            holder.imageView.setBackgroundResource(R.drawable.checkbox_off_background);
                        }

                    }
                }
            });

        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageBean item = this.messageList.get(position);
        holder.messageName.setText(item.getTitle());
        holder.imageView.setBackgroundResource(R.drawable.checkbox_off_background);
        if(manage==0) holder.imageView.setVisibility(View.GONE);
        else holder.imageView.setVisibility(View.VISIBLE);
    }



    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
