package com.example.shanpin.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.shanpin.Adapter.ScoreAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.MsgContentBean;
import com.example.shanpin.bean.UserBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.ToastUtil;
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

public class ScoreActivity extends AppCompatActivity {
    private static final String TAG ="ScoreActivity" ;
    private RecyclerView recyclerView;
    private ScoreAdapter adapter;
    private List<UserBean> userBeanList=new ArrayList<UserBean>();
    private String mode;
    private String pinID;
    private Toolbar toolbar;
    private  String userID;

private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        mode=getIntent().getStringExtra("mode");
        pinID=getIntent().getStringExtra("pinID");
        userID = AccountUtil.getAccount(getApplicationContext());
        toolbar = findViewById(R.id.activity_score_toolbar);


        if(mode.equals("0")) {
            toolbar.setTitle("加入人员");
        }else if(mode.equals("1")){
            toolbar.setTitle("信誉评分");
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.activity_score_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter=new ScoreAdapter(userBeanList,this,mode);
        recyclerView.setAdapter(adapter);

        getUserList(pinID);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE,  Menu.FIRST+1 , 0, "发送").setShowAsAction(1);
        if(mode.equals("2")) menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case Menu.FIRST+1:
                for(int i=0;i<userBeanList.size();i++){
                    sendScore(pinID,userID,userBeanList.get(i).getId()+"",userBeanList.get(i).getScore()+"");
                }
                finish();
                break;
            default:
                break;
        }
        return true;
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


    private void getUserList(String pinID){
        final RequestBody requestBody=new FormBody.Builder()
                .add("pinID",pinID)
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("PostActivity", "onResponse: "+responseText);
                if (!(responseText.equals("fail")||responseText.equals(""))){
                    List<UserBean> userBeanList_temp =new Gson().fromJson(responseText,new TypeToken<ArrayList<UserBean>>(){}.getType());
                    for(int i=0;i<userBeanList_temp.size();i++){
                        if(userID.equals(""+userBeanList_temp.get(i).getId())){
                            userBeanList_temp.remove(i);
                            break;
                        }
                    }
                    userBeanList.addAll(userBeanList_temp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(getApplicationContext(),responseText);
                        }
                    });
                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/GetUserList";
        Okhttp.sentPost(url,requestBody,callback);
    }

}