package com.example.shanpin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.shanpin.R;
import com.example.shanpin.bean.MsgContentBean;
import com.example.shanpin.bean.UserBean;
import com.example.shanpin.ui.ShowUserMsgActivity;
import com.example.shanpin.util.PictureUtil;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder>{
    private List<UserBean> mMsgList;
    private String  UserID;
    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleRatingBar simpleRatingBar;
        TextView name;
        TextView email;
        ImageView icon;

        public ViewHolder(View view) {
            super(view);
            icon=view.findViewById(R.id.item_score_icon);
            name=view.findViewById(R.id.item_score_name);
            email=view.findViewById(R.id.item_score_email);
            simpleRatingBar=view.findViewById(R.id.item_score_get_score);
        }

    }

    public ScoreAdapter(List<UserBean> msgList,Activity activity) {
        this.mMsgList=msgList;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score,parent,false);
        final ViewHolder holder = new ViewHolder(view);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserBean userBean = mMsgList.get(position);
        holder.name.setText(userBean.getName());
        holder.email.setText("邮箱: "+userBean.getEmail());
        /*
        if ((!PictureUtil.readIconFromFile(activity.getApplicationContext(), holder.icon,""+userBean.getId())) && userBean.getIcon() != null && (!userBean.getIcon().isEmpty())) {
            PictureUtil.downloadImage(userBean.getIcon(), PictureUtil.getIconPath(activity,""+userBean.getId()), activity, holder.icon);
        }
        */
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }



}
