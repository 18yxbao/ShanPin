package com.example.shanpin.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shanpin.Adapter.MsgAdapter;
import com.example.shanpin.Adapter.PinListAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.MsgContentBean;
import com.example.shanpin.bean.PinBean;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.PictureUtil;
import com.example.shanpin.util.ToastUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {
    private static final String TAG ="PostActivity" ;
    private Toolbar toolbar;
    private TextView P_name;
    private TextView P_title;
    private TextView P_content;
    private TextView P_member;
    private TextView P_genderLimit;
    private TextView P_timeStart;
    private TextView P_timeEnd;
    private ImageView imageView;

    private RecyclerView recyclerView;

    private String pinID;
    private View rootView;
    private PinBean pinBean;

    private MsgAdapter adapter;
    private List<MsgContentBean> mMsgList=new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //toolbar栏
        toolbar = findViewById(R.id.activity_post_toolbar);
        toolbar.setTitle("拼详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_post, null);


        P_name = (TextView) findViewById(R.id.Post_Host_name);
        P_title = (TextView) findViewById(R.id.Post_title);
        P_content = (TextView) findViewById(R.id.Post_Content);
        P_member = (TextView) findViewById(R.id.Post_Member_Limit);
        P_genderLimit = (TextView) findViewById(R.id.Post_Gender_Limit);
        P_timeStart = (TextView) findViewById(R.id.Post_timeStart);
        P_timeEnd = (TextView) findViewById(R.id.Post_End_Time);
        imageView=findViewById(R.id.Post_Host_icon);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView=findViewById(R.id.activity_post_recyclerview);
        recyclerView.setLayoutManager(layoutManager);


        pinID=getIntent().getStringExtra("pinID");
        String userID=getIntent().getStringExtra("userID");
        adapter= new MsgAdapter(mMsgList,userID, this);
        recyclerView.setAdapter(adapter);

        getPin(pinID);


        //点击发送评论！！！
    }

    //Toolbar item 按键响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                break;
        }
        return true;
    }

    //将pinBean的东西设置到相应的控件上
    private void showDetail(PinBean pinBean){
        P_name.setText(pinBean.getUserName());
        P_title.setText(pinBean.getTitle());
        P_content.setText(pinBean.getContent());
        String memberText =  pinBean.getMember_min() + "~" + pinBean.getMember_max() + "人";
        P_member.setText(memberText);
        String genderText = pinBean.getGender_limit();
        switch (genderText) {
            case "0":
                P_genderLimit.setText("无限制");
            case "1":
                P_genderLimit.setText("限男生");
            case "2":
                P_genderLimit.setText("限女生");
            default:
                P_genderLimit.setText("无限制");
        }
        P_timeStart.setText(pinBean.getTime_start());
        P_timeEnd.setText(pinBean.getTime_end());
        String pictureUrl=pinBean.getPicture();
        if(pictureUrl!=null && !pictureUrl.isEmpty()){
            PictureUtil.loadImageFromUrl(PostActivity.this,imageView,pictureUrl);
        }
    }

    private void getPin(String pinID){
        final RequestBody requestBody=new FormBody.Builder()
                .add("pinID",pinID)
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PostActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("PostActivity", "onResponse: "+responseText);
                if (!(responseText.equals("fail")||responseText.equals(""))){
                    pinBean=new Gson().fromJson(responseText,PinBean.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDetail(pinBean);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(PostActivity.this,responseText);
                        }
                    });
                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/GetPin";
        Okhttp.sentPost(url,requestBody,callback);
    }



}