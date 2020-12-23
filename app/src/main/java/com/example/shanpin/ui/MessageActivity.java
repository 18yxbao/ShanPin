package com.example.shanpin.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.shanpin.Adapter.MsgAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.MsgContentBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.ToastUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG ="MessageActivity";
    private Toolbar toolbar;
    private TextInputEditText editText;
    private Button sendButton;

    private RecyclerView recyclerView;
    private MsgAdapter adapter;
    private List<MsgContentBean> mMsgList=new ArrayList<>();
    private String pinID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.activity_post_toolbar);
        toolbar.setTitle("拼详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText=findViewById(R.id.activity_message_editText);
        sendButton=findViewById(R.id.activity_message_send);
        recyclerView=findViewById(R.id.activity_message_recyclerview);
        adapter=new MsgAdapter(mMsgList, AccountUtil.getAccount(MessageActivity.this),MessageActivity.this);
        recyclerView.setAdapter(adapter);

        pinID=getIntent().getStringExtra("pinID");
        getTalk(pinID,"1");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if (!content.equals("")) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());
                    String time = formatter.format(curDate);
                    MsgContentBean msgContentBean = new MsgContentBean();
                    msgContentBean.setName(AccountUtil.getUserInfo(MessageActivity.this).getName());
                    msgContentBean.setPinID(pinID);
                    msgContentBean.setUserID(AccountUtil.getAccount(MessageActivity.this));
                    msgContentBean.setTime(time);
                    msgContentBean.setContent(content);
                    msgContentBean.setIs_public("1");
                    sendTalk(msgContentBean);
                    mMsgList.add(msgContentBean);
                    adapter.notifyItemInserted(0);
                    editText.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "发送内容为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void getTalk(String pinID,String is_public){
        final RequestBody requestBody=new FormBody.Builder()
                .add("pinID",pinID)
                .add("is_public",is_public)
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MessageActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("PostActivity", "onResponse: "+responseText);
                if (!(responseText.equals("fail")||responseText.equals(""))){
                    List<MsgContentBean> mMsgList_temp =new Gson().fromJson(responseText,new TypeToken<ArrayList<MsgContentBean>>(){}.getType());
                    mMsgList.addAll(mMsgList_temp);
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
                            ToastUtil.showShort(MessageActivity.this,responseText);
                        }
                    });
                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/GetTalk";
        Okhttp.sentPost(url,requestBody,callback);
    }

    private void sendTalk(final MsgContentBean msgContentBean){
        final RequestBody requestBody=new FormBody.Builder()
                .add("pinID",msgContentBean.getPinID())
                .add("userID",msgContentBean.getUserID())
                .add("time",msgContentBean.getTime())
                .add("content",msgContentBean.getContent())
                .add("is_public",msgContentBean.getIs_public())
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MessageActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d(TAG, "sendTalk:onResponse: "+responseText);

            }
        };
        String url="http://119.29.136.236:8080/ShanPin/SendTalk";
        Okhttp.sentPost(url,requestBody,callback);
    }

}