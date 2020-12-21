package com.example.shanpin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shanpin.bean.MsgContentBean;

import com.example.shanpin.R;
import com.example.shanpin.util.PictureUtil;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    private List<MsgContentBean> mMsgList;
    private String  UserID;
    private Activity activity;
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rihgtMsg;
        TextView leftName;
        TextView rightName;
        ImageView leftIcon;
        ImageView rightIcon;
        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.msg_left_msgitem);
            rightLayout = (LinearLayout) view.findViewById(R.id.msg_right_msgitem);
            leftMsg = (TextView) view.findViewById(R.id.msg_left_msg);
            rihgtMsg = (TextView) view.findViewById(R.id.msg_right_msg);
            leftName=view.findViewById(R.id.msg_left_name);
            rightName=view.findViewById(R.id.msg_right_name);
            leftIcon=view.findViewById(R.id.msg_left_icon);
            rightIcon=view.findViewById(R.id.msg_right_icon);
        }
    }

    public MsgAdapter(List<MsgContentBean> msgList, String UserID, Activity activity) {
        mMsgList = msgList;
        this.UserID=UserID;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MsgContentBean msg = this.mMsgList.get(position);

        if (UserID.equals(msg.getUserID())) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            holder.leftName.setText(msg.getName());
            if ((!PictureUtil.readIconFromFile(activity.getApplicationContext(), holder.leftIcon)) && msg.getIcon() != null && (!msg.getIcon().isEmpty())) {
                PictureUtil.downloadImage(msg.getIcon(), PictureUtil.getIconPath(activity), activity, holder.leftIcon);
            }
        }
        else {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rihgtMsg.setText(msg.getContent());
            holder.rightName.setText(msg.getName());
            if ((!PictureUtil.readIconFromFile(activity.getApplicationContext(), holder.rightIcon)) && msg.getIcon() != null && (!msg.getIcon().isEmpty())) {
                PictureUtil.downloadImage(msg.getIcon(), PictureUtil.getIconPath(activity), activity, holder.rightIcon);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}
