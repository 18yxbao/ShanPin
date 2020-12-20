package com.example.shanpin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shanpin.R;
import com.example.shanpin.bean.PinBean;

import java.util.List;

public class PinListAdapter extends RecyclerView.Adapter<PinListAdapter.ViewHolder> {
    private List<PinBean> pinList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView itemImage;
        TextView itemName;
        TextView itemMaster;
        TextView itemTime;

        public ViewHolder (View view)
        {
            super(view);
            itemImage = (ImageView) view.findViewById(R.id.item_pin_image);
            itemName = (TextView) view.findViewById(R.id.item_pin_title);
            itemMaster=view.findViewById(R.id.item_pin_author);
            itemTime=view.findViewById(R.id.item_pin_time);
        }
    }

    public PinListAdapter(List <PinBean> itemList){
        this.pinList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pin,parent,false);
        //holder.itemView.setOnClickListener();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        PinBean item = this.pinList.get(position);
        holder.itemName.setText(item.getTitle());
        holder.itemMaster.setText(item.getUserID());
        holder.itemTime.setText(item.getTime_start());
    }

    @Override
    public int getItemCount() {
        return this.pinList.size();
    }
}
