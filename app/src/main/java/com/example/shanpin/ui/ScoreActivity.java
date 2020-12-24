package com.example.shanpin.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.shanpin.Adapter.ScoreAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.MsgContentBean;
import com.example.shanpin.bean.UserBean;
import com.example.shanpin.util.Okhttp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScoreActivity extends AppCompatActivity {
    private static final String TAG ="ScoreActivity" ;
    private RecyclerView recyclerView;
    private ScoreAdapter adapter;
    private List<UserBean> userBeanList=new ArrayList<UserBean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.activity_score_recyclerview);
        recyclerView.setLayoutManager(layoutManager);

        UserBean userBean = new UserBean();
        userBean.setName("1");
        userBeanList.add(userBean);
        userBean = new UserBean();
        userBean.setName("2");
        userBeanList.add(userBean);
        adapter=new ScoreAdapter(userBeanList,this);
        recyclerView.setAdapter(adapter);

    }

    private void sendScore(String pinID,String from_userID,String to_userID,String score){
        final RequestBody requestBody=new FormBody.Builder()
                .add("pinID",pinID)
                .add("from_userID",from_userID)
                .add("to_userID",to_userID)
                .add("score",score)
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScoreActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScoreActivity.this,responseText,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/SendScore";
        Okhttp.sentPost(url,requestBody,callback);
    }

}