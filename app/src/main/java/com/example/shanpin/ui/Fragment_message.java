package com.example.shanpin.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shanpin.Adapter.MessageListAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.MessageBean;
import com.example.shanpin.bean.MsgContentBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.PictureUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_message extends Fragment {


    //定义一个帖子列表作为数据源
    private List<MessageBean> messageBeanList = new ArrayList<MessageBean>();
    private RecyclerView recyclerView;
    private MessageListAdapter messageListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView = view.findViewById(R.id.fragment_message__recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        messageListAdapter = new MessageListAdapter(messageBeanList);
        recyclerView.setAdapter(messageListAdapter);
        swipeRefreshLayout = view.findViewById(R.id.fragment_message_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                messageBeanList.clear();
                getTalkList();
                getWaitScoreList();
            }
        });
        getTalkList();
        getWaitScoreList();
        return view;
    }


    private void getTalkList() {
        RequestBody requestBody = new FormBody.Builder()
                .add("userID", AccountUtil.getAccount(getActivity()))
                .build();
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "请检查你的网络", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("getTalkList", "onResponse: " + "请检查你的网络");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("Fragment_message", "getTalkList:onResponse: " + responseText);
                swipeRefreshLayout.setRefreshing(false);
                if (!(responseText.equals("fail") || responseText.equals(""))) {
                    List<MessageBean> list = new Gson().fromJson(responseText, new TypeToken<ArrayList<MessageBean>>() {
                    }.getType());
                    for(int i=0;i<list.size();i++){
                        list.get(i).setStatus("1");
                    }
                    messageBeanList.addAll(list);
//                    List<MessageBean> tempList=new Gson().fromJson(responseText,new TypeToken<ArrayList<MessageBean>>(){}.getType());
//                    messageBeanList.addAll(tempList);
                    Log.d("Fragment_message", "getTalkList:messageBeanList: " + messageBeanList.toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            messageListAdapter.notifyDataSetChanged();
                            messageListAdapter.setMessageList(messageBeanList);
                        }
                    });

                }
            }
        };
        String url = "http://119.29.136.236:8080/ShanPin/GetTalkList";
        Okhttp.sentPost(url, requestBody, callback);
    }

    private void getWaitScoreList() {
        RequestBody requestBody = new FormBody.Builder()
                .add("userID", AccountUtil.getAccount(getActivity()))
                .build();
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "请检查你的网络", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("getTalkList", "onResponse: " + "请检查你的网络");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("Fragment_message", "getTalkList:onResponse: " + responseText);
                swipeRefreshLayout.setRefreshing(false);
                if (!(responseText.equals("fail") || responseText.equals(""))) {
                    List<MessageBean> list = new Gson().fromJson(responseText, new TypeToken<ArrayList<MessageBean>>() {
                    }.getType());
                    messageBeanList.addAll(list);
//                    List<MessageBean> tempList=new Gson().fromJson(responseText,new TypeToken<ArrayList<MessageBean>>(){}.getType());
//                    messageBeanList.addAll(tempList);
                    Log.d("Fragment_message", "getTalkList:messageBeanList: " + messageBeanList.toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            messageListAdapter.notifyDataSetChanged();
                            messageListAdapter.setMessageList(messageBeanList);
                        }
                    });

                }
            }
        };
        String url = "http://119.29.136.236:8080/ShanPin/GetWaitScoreList";
        Okhttp.sentPost(url, requestBody, callback);
    }

}
