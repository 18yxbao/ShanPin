package com.example.shanpin.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.shanpin.Adapter.CreateAndJoinAdapter;
import com.example.shanpin.Adapter.MessageListAdapter;
import com.example.shanpin.Adapter.MsgAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.MessageBean;
import com.example.shanpin.bean.MsgContentBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 在设置界面看自己创建或者加入的pinlist的activity
 */
public class CreateAndJoinActivity extends AppCompatActivity {

    private static final String TAG ="CreateAndJoinActivity";
    private Toolbar toolbar;

    private List<MessageBean> messageBeanList= new ArrayList<MessageBean>();
    private RecyclerView recyclerView;
    private CreateAndJoinAdapter createAndJoinAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String mode;    //"0"是创建的拼，"1"是加入的拼
    private String suffix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_and_join);

        mode=getIntent().getStringExtra("mode");

        toolbar = findViewById(R.id.activity_createandjion_toolbar);
        if(mode.equals("0")){
            toolbar.setTitle("我创建的拼");
            suffix="GetCreatePinList";
        }else {
            toolbar.setTitle("我加入的拼");
            suffix="GetJoinPinList";
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView=findViewById(R.id.activity_createandjion__recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CreateAndJoinActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        createAndJoinAdapter=new CreateAndJoinAdapter(messageBeanList);
        recyclerView.setAdapter(createAndJoinAdapter);

        swipeRefreshLayout=findViewById(R.id.activity_createandjion_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getList(suffix);
            }
        });
        getList(suffix);
    }

    private void getList(String suffix){
        RequestBody requestBody=new FormBody.Builder()
                .add("userID",AccountUtil.getAccount(CreateAndJoinActivity.this))
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreateAndJoinActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("getTalkList", "onResponse: "+"请检查你的网络");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("Fragment_message", "getTalkList:onResponse: "+responseText);
                swipeRefreshLayout.setRefreshing(false);
                if (!(responseText.equals("fail")||responseText.equals(""))){
                    messageBeanList=new Gson().fromJson(responseText,new TypeToken<ArrayList<MessageBean>>(){}.getType());

//                    List<MessageBean> tempList=new Gson().fromJson(responseText,new TypeToken<ArrayList<MessageBean>>(){}.getType());
//                    messageBeanList.addAll(tempList);
                    Log.d("Fragment_message", "getTalkList:messageBeanList: "+messageBeanList.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            messageListAdapter.notifyDataSetChanged();
                            createAndJoinAdapter.setMessageList(messageBeanList);
                        }
                    });

                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/"+suffix;
        Okhttp.sentPost(url,requestBody,callback);
    }
}