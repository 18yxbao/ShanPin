package com.example.shanpin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shanpin.Adapter.PinListAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.PinBean;
import com.example.shanpin.bean.TagBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.ToastUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CertificatePinner;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_home extends Fragment {
    private List<TagBean> tagList= new ArrayList<TagBean>();
    private List<Button> tagButtonList = new ArrayList<Button>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private PinListAdapter adapter;
    private LinearLayout tag_layout;
    private ArrayList<PinBean> pinBeanArrayList=new ArrayList<PinBean>();
    private String tagID="0";

    public Fragment_home(){
        initItems();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home,container,false);
        swipeRefreshLayout=view.findViewById(R.id.pin_RefreshLayout);
        floatingActionButton=view.findViewById(R.id.fragment_home_floatingActionButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_home_recycler_view);
        tag_layout=view.findViewById(R.id.fragment_home_tag_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PinListAdapter(getActivity(),pinBeanArrayList);
        recyclerView.setAdapter(adapter);

        initTag();
        getPinList(null);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Map<String,String> map=new HashMap<>();
                map.put("tagID",tagID);
                getPinList(map);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),PublishActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initTag(){
        for(int i=0 ; i< tagList.size();i++) {
            Button button = new Button(getContext());
            button.setText(tagList.get(i).getName());
            button.setTag(tagList.get(i));
            button.setOnClickListener(tag_click);
            button.setBackgroundResource(R.color.color_background_grey);
            tagButtonList.add(button);
            tag_layout.addView(button);
        }
    }


    private View.OnClickListener tag_click =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (int i = 0; i < tagButtonList.size(); i++) {
                Button button = tagButtonList.get(i);
                button.setBackgroundResource(R.color.color_background_grey);
            }
            view.setBackgroundResource(R.color.colorPrimary);
            TagBean tag = (TagBean) view.getTag();
            Toast.makeText(getContext(), tag.getName(), Toast.LENGTH_SHORT).show();
            tagID=tag.getID();
            Map<String, String> map = new HashMap<>();
            map.put("tagID",tag.getID());
            getPinList(map);
        }
    };

    private void getPinList(Map<String,String> map){
        FormBody.Builder builder=new FormBody.Builder();
        if(map!=null&& !map.isEmpty()){
            for (String key:map.keySet()) {
                String value=map.get(key);
                if (!(value==null||value.isEmpty()))
                    builder.add(key,value);
            }
        }
        final RequestBody requestBody=builder.build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("login", "onResponse: "+"请检查你的网络");
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("Pin", "onResponse: "+responseText);
                if (!(responseText.equals("fail")||responseText.equals(""))){
                    if(pinBeanArrayList!=null){
                        pinBeanArrayList.clear();
                    }
                    pinBeanArrayList=new Gson().fromJson(responseText,new TypeToken<ArrayList<PinBean>>(){}.getType());
                    adapter.setPinList(pinBeanArrayList);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(getContext(),responseText);
                        }
                    });
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/GetPinList";
        Okhttp.sentPost(url,requestBody,callback);
    }



    private void initItems() {
        TagBean tag0 = new TagBean();
        tag0.setID("0");
        tag0.setName("全部");
        tagList.add(tag0);
        TagBean tag1 = new TagBean();
        tag1.setID("1");
        tag1.setName("拼车");
        tagList.add(tag1);
        TagBean tag2 = new TagBean();
        tag2.setID("2");
        tag2.setName("拼单");
        tagList.add(tag2);
        TagBean tag3 = new TagBean();
        tag3.setID("3");
        tag3.setName("拼自习室");
        tagList.add(tag3);
        TagBean tag4 = new TagBean();
        tag4.setID("4");
        tag4.setName("拼运动");
        tagList.add(tag4);
        TagBean tag5 = new TagBean();
        tag5.setID("5");
        tag5.setName("拼旅游");
        tagList.add(tag5);
        TagBean tag6 = new TagBean();
        tag6.setID("6");
        tag6.setName("拼其他");
        tagList.add(tag6);
    }
}
