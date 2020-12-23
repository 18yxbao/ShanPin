package com.example.shanpin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shanpin.R;
import com.example.shanpin.bean.UserBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Button login_btn;
    private Button register_btn;
    private LinearLayout login_layout;
    private TextView welcome;
    private EditText txtUserName;
    private EditText txtPassWord;

    private String loginUser;
    private String loginPass;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn = (Button) findViewById(R.id.activity_login_login_btn);
        register_btn = (Button) findViewById(R.id.activity_login_register_btn);
        txtUserName = (EditText) findViewById(R.id.activity_login_email);
        txtPassWord = (EditText) findViewById(R.id.activity_login_password);
        welcome=findViewById(R.id.activity_login_welcome);
        login_layout=findViewById(R.id.activity_login_layout_login);

        IsPass();

        login_btn.setOnClickListener(click);
        register_btn.setOnClickListener(click);
    }
    private void IsPass(){
        if(AccountUtil.getLog(getApplicationContext())==true){
            UserBean userBean = AccountUtil.getUserInfo(getApplicationContext());
            Log.d("TAG", "IsPass: "+userBean.toString());
            getLogin(userBean.getEmail(),userBean.getPassword());
            login_layout.setVisibility(View.GONE);
            welcome.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            switch(view.getId()){
                case R.id.activity_login_login_btn:
                    if (txtUserName.getText().toString().equals("")) {
                        Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    } else if (txtPassWord.getText().toString().equals("")) {
                        Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    } else {
                        loginUser = txtUserName.getText().toString().trim();
                        loginPass = txtPassWord.getText().toString().trim();


                        getLogin(loginUser,loginPass);

                    }
                    break;
                case R.id.activity_login_register_btn:
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, RegisterActivity.class);
                    LoginActivity.this.startActivity(intent);
                    break;

            }
        }
    };

    //登录成功返回userID
    private void getLogin(String loginUser,String loginPass){
        final RequestBody requestBody=new FormBody.Builder()
                .add("email",loginUser)
                .add("password",loginPass)
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("login", "onResponse: "+"请检查你的网络");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("login", "onResponse: "+responseText);
                if (!(responseText.equals("fail")||responseText.equals(""))){

                    AccountUtil.setAccount(getApplicationContext(),responseText);
                    AccountUtil.setLog(getApplicationContext(),true);

                    getUserInfo(responseText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/Login";
        Okhttp.sentPost(url,requestBody,callback);
    }

    private void getUserInfo(String userID){
        UserBean userBean=new UserBean();
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

                    UserBean userBean = new Gson().fromJson(responseText,UserBean.class);
                    AccountUtil.setUserInfo(getApplicationContext(),userBean);

                    Intent intent=new Intent(LoginActivity.this,EnterActivity.class);
                    intent.putExtra("UserID",responseText);
                    startActivity(intent);
                    finish();
                }

            }
        };
        String url="http://119.29.136.236:8080/ShanPin/GetUserInfo";
        Okhttp.sentPost(url,requestBody,callback);
    }
}