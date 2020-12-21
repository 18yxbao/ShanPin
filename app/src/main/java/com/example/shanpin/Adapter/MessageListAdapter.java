package com.example.shanpin.Adapter;

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

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder>{

    private List<MessageBean> messageList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView messageImage;
        TextView messageName;
        TextView messageContent;
        TextView messageTime;

        public ViewHolder (View view)
        {
            super(view);
            messageImage = (ImageView) view.findViewById(R.id.item_message_Image);
            messageName = (TextView) view.findViewById(R.id.item_message_Name);
            messageContent=view.findViewById(R.id.item_message_Content);
            messageTime=view.findViewById(R.id.item_message_Date);
        }
    }

    public MessageListAdapter(List<MessageBean> itemList){
        this.messageList = itemList;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
//        view.setOnClickListener();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        MessageBean item = this.messageList.get(position);
        holder.messageContent.setText(item.getContent());
        holder.messageName.setText(item.getTitle());
        holder.messageTime.setText(item.getTime());

        if (mOnItemClickLitener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);

                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return this.messageList.size();
    }
}
