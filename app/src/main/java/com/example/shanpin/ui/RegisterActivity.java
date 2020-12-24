package com.example.shanpin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shanpin.R;
import com.example.shanpin.bean.UserBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.ActionTrigger;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private Button register_btn;
    private Button cancleRegist_btn;
    private EditText userEmail;
    private EditText verificationCode;
    private EditText register_pwd;
    private EditText register_pwd2;
    private Button sendVeriCode;


    private String email;
    private String verifiCode;
    private String password1;
    private String password2;
    private ActionTrigger actionTrigger=new ActionTrigger(3000);
    private ActionTrigger actionTrigger2=new ActionTrigger(5000);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisit);

        userEmail = (EditText) findViewById(R.id.activity_register_userEmail);
        verificationCode = (EditText) findViewById(R.id.activity_register_verificationCode);
        register_pwd = (EditText) findViewById(R.id.activity_register_register_pwd);
        register_pwd2 = (EditText) findViewById(R.id.activity_register_register_pwd2);
        sendVeriCode = (Button) findViewById(R.id.activity_register_sendVeriCode);
        register_btn = (Button) findViewById(R.id.activity_register_register_btn);
        cancleRegist_btn = (Button) findViewById(R.id.activity_register_cancle_btn);


        sendVeriCode.setOnClickListener(click);
        register_btn.setOnClickListener(click);
        cancleRegist_btn.setOnClickListener(click);
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.activity_register_sendVeriCode:
                    if (!actionTrigger2.canTrigger()){
                        ToastUtil.showShort(RegisterActivity.this,"点击太频繁！");
                        break;
                    }
                    email = userEmail.getText().toString().trim();
                    String[] splitString =email.split("@");
                    if(email.equals("")){
                        Toast.makeText(getApplicationContext(),"邮箱不能为空",Toast.LENGTH_SHORT).show();
                    }else if(splitString.length<2 || !splitString[1].equals("stu.edu.cn")){
                        Toast.makeText(getApplicationContext(),"请输入汕大邮箱",Toast.LENGTH_SHORT).show();
                    }else{
                        PublishCode(email);
                    }

                    break;
                case R.id.activity_register_register_btn:
                    if (!actionTrigger.canTrigger()){
                        ToastUtil.showShort(RegisterActivity.this,"点击太频繁！");
                        break;
                    }
                    email = userEmail.getText().toString().trim();
                    verifiCode = verificationCode.getText().toString().trim();
                    password1 = register_pwd.getText().toString().trim();
                    password2 = register_pwd2.getText().toString().trim();
                    if (password1.equals(password2)) {
                        Register(email, password1,verifiCode );
                    }
                    break;
                case R.id.activity_register_cancle_btn:
                    finish();
                    break;
            }
        }
    };


    private void PublishCode(String email) {
        final RequestBody requestBody = new FormBody.Builder().add("email", email).build();
        Toast.makeText(RegisterActivity.this, "我点了", Toast.LENGTH_SHORT).show();
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("register", "onResponse: 请检查你的网络");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
                    }
                });
//                        Toast.makeText(RegisterActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d("register", "onResponse: " + responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(responseText.equals("exist")){
                            Toast.makeText(RegisterActivity.this, "用户已存在，请直接登录", Toast.LENGTH_SHORT).show();
                        }else if(responseText.equals("success")){
                            Toast.makeText(RegisterActivity.this, "发送成功，请登录邮箱查看", Toast.LENGTH_SHORT).show();
                        }else if(responseText.equals("wait")){
                            Toast.makeText(RegisterActivity.this, "验证码还在有效期，请查看邮件", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        };
        String url = "http://119.29.136.236:8080/ShanPin/PublishCode";
//                String url="http://192.168.112.240:8080/ShanPin/PublishCode";
//                String url="http://172.16.212.200:8080/ShanPin/PublishCode";
        Okhttp.sentPost(url, requestBody, callback);
    }
    private void Register(final String email, final String password1, String verifiCode) {
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password1)
                .add("check_code", verifiCode)
                .build();
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                            Toast.makeText(RegisterActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(responseText.equals("retry")){
                            Toast.makeText(RegisterActivity.this, "验证码已过期，请重新获取", Toast.LENGTH_SHORT).show();
                        }else if(responseText.equals("error")){
                            Toast.makeText(RegisterActivity.this, "验证码输入错误", Toast.LENGTH_SHORT).show();
                        }else if(responseText.equals("")){
                            Toast.makeText(RegisterActivity.this, "啥玩意出错了", Toast.LENGTH_SHORT).show();
                        }else if(responseText.equals("exist")) {
                            Toast.makeText(RegisterActivity.this, "用户已存在，请直接登录", Toast.LENGTH_SHORT).show();
                        }else{ //注册成功会返回用户ID
                                AccountUtil.setAccount(getApplicationContext(),responseText);
                                Toast.makeText(RegisterActivity.this, "注册成功，你的用户ID是："+responseText, Toast.LENGTH_SHORT).show();
                                UserBean userBean=new UserBean();
                                userBean.setEmail(email);
                                userBean.setPassword(password1);
//                            userBean.setId(responseText);

                                AccountUtil.setUserInfo(getApplicationContext(),userBean);
                                Intent intent = new Intent(getApplicationContext(),SetUserInfoActivity.class);
                                startActivity(intent);
                                finish();
                        }
                    }
                });

            }
        };
        String url = "http://119.29.136.236:8080/ShanPin/Register";
        Okhttp.sentPost(url, requestBody, callback);
    }

}
