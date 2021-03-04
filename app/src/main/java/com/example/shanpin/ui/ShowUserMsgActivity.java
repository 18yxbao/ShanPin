package com.example.shanpin.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shanpin.R;
import com.example.shanpin.bean.UserBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.PictureUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowUserMsgActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout set_icon;
    private LinearLayout set_name;
    private LinearLayout set_email;
    private LinearLayout set_gender;
    private LinearLayout set_grade;
    private LinearLayout set_score;
    private ImageView imageView;
    private TextView T_name;
    private TextView T_email;
    private TextView T_gender;
    private TextView T_grade;
    private TextView T_score;
    private Button savemsg;

    private UserBean userBean;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setuserinfo);

        //toolbar栏
        toolbar = findViewById(R.id.activity_setuserinfo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserID =getIntent().getStringExtra("UserID");
        set_icon = (LinearLayout) findViewById(R.id.activity_setuserinfo_seticon);
        set_name = (LinearLayout) findViewById(R.id.activity_setuserinfo_setname);
        set_email = (LinearLayout) findViewById(R.id.activity_setuserinfo_setemail);
        set_gender = (LinearLayout) findViewById(R.id.activity_setuserinfo_setgender);
        set_grade = (LinearLayout) findViewById(R.id.activity_setuserinfo_setgrade);
        set_score = (LinearLayout) findViewById(R.id.activity_setuserinfo_setscore);
        imageView = findViewById(R.id.activity_setuserinfo_icon);
        T_name = (TextView) findViewById(R.id.activity_setuserinfo_name);
        T_email = (TextView) findViewById(R.id.activity_setuserinfo_email);
        T_gender = (TextView) findViewById(R.id.activity_setuserinfo_gender);
        T_grade = (TextView) findViewById(R.id.activity_setuserinfo_grade);
        T_score = (TextView) findViewById(R.id.activity_setuserinfo_score);
        savemsg = (Button) findViewById(R.id.activity_setuserinfo_save);
        savemsg.setVisibility(View.GONE);

        getUserInfo(UserID);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                break;
        }
        return true;
    }

    private void getUserInfo(String userID){
        Log.d("TAG", "getUserInfo: "+userID);
        RequestBody requestBody=new FormBody.Builder()
                .add("id",userID)
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                Log.d("login", "onResponse: "+"请检查你的网络");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("login", "onResponse: "+responseText);
                if (!(responseText.equals("fail")||responseText.equals(""))){
                    userBean = new Gson().fromJson(responseText,UserBean.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toolbar.setTitle(userBean.getName()+"的信息");
                            T_name.setText(userBean.getName());
                            T_email.setText(userBean.getEmail());
                            T_gender.setText(userBean.getGender());
                            T_grade.setText(userBean.getGrade());
                            int term=(int)(userBean.getScore()*100);
                            T_score.setText("" + term/100.0);
                            PictureUtil.downloadImage(userBean.getIcon(), PictureUtil.getIconPath(getApplicationContext(),""+userBean.getId()), ShowUserMsgActivity.this, imageView);
                        }
                    });
                }

            }
        };
        String url="http://119.29.136.236:8080/ShanPin/GetUserInfo";
        Okhttp.sentPost(url,requestBody,callback);
    }
}