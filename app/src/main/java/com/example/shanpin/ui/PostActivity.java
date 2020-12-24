package com.example.shanpin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shanpin.Adapter.MsgAdapter;
import com.example.shanpin.R;
import com.example.shanpin.bean.MsgContentBean;
import com.example.shanpin.bean.PinBean;
import com.example.shanpin.bean.UserBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.PictureUtil;
import com.example.shanpin.util.ToastUtil;
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
    private ImageView icon;

    private EditText editText;
    private Button sendButton;

    private RecyclerView recyclerView;

    private String pinID;
    private View rootView;
    private PinBean pinBean;

    private MsgAdapter adapter;
    private List<MsgContentBean> mMsgList=new ArrayList<>();

    private List<UserBean> userBeanList;


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
        imageView = findViewById(R.id.activity_post_imageView);
        icon = findViewById(R.id.Post_Host_icon);
        editText = findViewById(R.id.Post_Chat_EditText);
        sendButton = findViewById(R.id.Post_Chat_Send);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.activity_post_recyclerview);
        recyclerView.setLayoutManager(layoutManager);


        pinID = getIntent().getStringExtra("pinID");
        String userID = getIntent().getStringExtra("userID");
        adapter = new MsgAdapter(mMsgList, userID, this);
        recyclerView.setAdapter(adapter);

        getPin(pinID);
        getTalk(pinID, "0");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if (!content.equals("")) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());
                    String time = formatter.format(curDate);
                    MsgContentBean msgContentBean = new MsgContentBean();
                    msgContentBean.setName(AccountUtil.getUserInfo(PostActivity.this).getName());
                    msgContentBean.setPinID(pinID);
                    msgContentBean.setUserID(AccountUtil.getAccount(PostActivity.this));
                    msgContentBean.setTime(time);
                    msgContentBean.setContent(content);
                    msgContentBean.setIs_public("0");
                    sendTalk(msgContentBean);
                    mMsgList.add(msgContentBean);
                    adapter.notifyItemInserted(0);
                    editText.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "发送内容为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowUserMsgActivity.class);
                intent.putExtra("UserID",pinBean.getUserID());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE,  Menu.FIRST+1 , 0, "加入").setShowAsAction(1);
        return true;
    }


    //Toolbar item 按键响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case Menu.FIRST+1:
                JoinPIn(pinID,AccountUtil.getAccount(getApplicationContext()));
                break;
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

        String iconUrl=pinBean.getIcon();
        if(!PictureUtil.readIconFromFile(PostActivity.this,icon,pinBean.getUserID())
                && iconUrl!=null &&!iconUrl.isEmpty()){
            String filename=PictureUtil.getIconPath(PostActivity.this,pinBean.getUserID());
            PictureUtil.downloadImage(iconUrl,filename,PostActivity.this,icon);
        }
    }
    private void JoinPIn(String pinID,String UserID){
        final RequestBody requestBody=new FormBody.Builder()
                .add("userID",UserID)
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"加入成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"加入失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/JoinPin";
        Okhttp.sentPost(url,requestBody,callback);
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
                            ToastUtil.showShort(PostActivity.this,responseText);
                        }
                    });
                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/GetTalk";
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
                    userBeanList =new Gson().fromJson(responseText,new TypeToken<ArrayList<UserBean>>(){}.getType());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String userID = AccountUtil.getAccount(getApplicationContext());

                            for(int i=0;i<userBeanList.size();i++){
                                if(userID.equals(userBeanList.get(i).getId()+"")){

                                }
                            }

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
        String url="http://119.29.136.236:8080/ShanPin/GetUserList";
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
                        Toast.makeText(PostActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
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