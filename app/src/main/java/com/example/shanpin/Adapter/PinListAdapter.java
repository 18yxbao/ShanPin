package com.example.shanpin.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shanpin.R;
import com.example.shanpin.bean.PinBean;
import com.example.shanpin.ui.PostActivity;
import com.example.shanpin.util.AccountUtil;

import java.util.List;

public class PinListAdapter extends RecyclerView.Adapter<PinListAdapter.ViewHolder> {
    private List<PinBean> pinList;
    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder{
//        ImageView itemImage;
        TextView itemName;
        TextView itemMaster;
        TextView itemTime;
        LinearLayout linearLayout;

        public ViewHolder (View view)
        {
            super(view);
//            itemImage = (ImageView) view.findViewById(R.id.item_pin_image);
            itemName = (TextView) view.findViewById(R.id.item_pin_title);
            itemMaster=view.findViewById(R.id.item_pin_author);
            itemTime=view.findViewById(R.id.item_pin_time);
            linearLayout=view.findViewById(R.id.item_pin_linearlayout);
        }
    }

    public PinListAdapter(Activity activity,List <PinBean> itemList){
        this.activity=activity;
        this.pinList = itemList;
    }

    public void setPinList(List<PinBean> pinList) {
        this.pinList = pinList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pin,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Intent intent=new Intent(activity,PostActivity.class);
                intent.putExtra("pinID",pinList.get(position).getPinID());
                intent.putExtra("userID", AccountUtil.getAccount(activity));
                activity.startActivity(intent);
            }
        });
        //holder.itemView.setOnClickListener();
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        PinBean item = pinList.get(position);
        holder.itemName.setText(item.getTitle());
        holder.itemMaster.setText(item.getUserName());
        holder.itemTime.setText(item.getTime_start());
    }

    @Override
    public int getItemCount() {
        return this.pinList.size();
    }


}
