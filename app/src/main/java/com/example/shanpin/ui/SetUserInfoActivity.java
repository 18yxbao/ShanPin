package com.example.shanpin.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.shanpin.R;
import com.example.shanpin.bean.ImageBean;
import com.example.shanpin.bean.UserBean;
import com.example.shanpin.ui.tool.TwoPopupWindow;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.PictureUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SetUserInfoActivity extends AppCompatActivity{
    private static final String TAG ="SetUserInfoActivity" ;
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

    private static final int REQUEST_IMAGE_GET = 0; //
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final int REQUEST_NAME_GET = 4;
    private static final int REQUEST_EMAIL_GET = 5;
    private static final int REQUEST_GENDER_GET = 6;
    private static final int REQUEST_GRADE_GET = 7;

    private View rootView;

    private TwoPopupWindow genderPopupWindow;
    private TwoPopupWindow photoPopupWindow;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setuserinfo);

        //toolbar栏
        toolbar = findViewById(R.id.activity_setuserinfo_toolbar);
        toolbar.setTitle("设置个人信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //获得个人信息
        userBean = AccountUtil.getUserInfo(getApplicationContext());
        rootView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_setuserinfo, null);
        //声明
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

        //设置文字
        T_name.setText(userBean.getName());
        T_email.setText(userBean.getEmail());
        T_gender.setText(userBean.getGender());
        T_grade.setText(userBean.getGrade());
        int term=(int)(userBean.getScore()*100);
        T_score.setText("" + term/100.0);

        //设置点击事件
        set_icon.setOnClickListener(click);
        set_name.setOnClickListener(click);
        set_email.setOnClickListener(click);
        set_gender.setOnClickListener(click);
        set_grade.setOnClickListener(click);
        set_score.setOnClickListener(click);
        savemsg.setOnClickListener(click);

        photoPopupWindow =new TwoPopupWindow(SetUserInfoActivity.this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // 权限还没有授予，进行申请
                            ActivityCompat.requestPermissions(SetUserInfoActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    200); // 申请的 requestCode 为 200
                        }
                        else {
                            photoPopupWindow.dismiss();
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            // 判断系统中是否有处理该 Intent 的 Activity
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(intent, REQUEST_IMAGE_GET);
                            } else {
                                Toast.makeText(getApplicationContext(), "未找到图片查看器", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
// 拍照
                        // 拍照及文件权限检查
                        Log.d("pic", "你选了拍照");
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED
                                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // 权限还没有授予，进行申请
                            ActivityCompat.requestPermissions(SetUserInfoActivity.this,
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    300); // 申请的 requestCode 为 300
                        } else {
                            // 权限已经申请，直接拍照
                            photoPopupWindow.dismiss();
                            PictureUtil.startCamera(SetUserInfoActivity.this);
                        }

                    }
                });

        genderPopupWindow =new TwoPopupWindow(SetUserInfoActivity.this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userBean.setGender("女");
                        T_gender.setText(userBean.getGender());
                        genderPopupWindow.dismiss();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userBean.setGender("男");
                        T_gender.setText(userBean.getGender());
                        genderPopupWindow.dismiss();
                    }
                });

        genderPopupWindow.btn_camera.setText("男");
        genderPopupWindow.btn_select.setText("女");
    }

    @Override
    protected void onResume() {
        super.onResume();

        String imageFilename=PictureUtil.getIconPath(SetUserInfoActivity.this);
        File imageFile=new File(imageFilename);
        Log.d(TAG, "onResume: "+imageFile.exists());
        if (imageFile.exists()){
            PictureUtil.readIconFromFile(getApplicationContext(), imageView);//从本地读取头像
        }else{
            String iconUrl=userBean.getIcon();
            Log.d(TAG, "onResume: "+iconUrl);
            if(iconUrl!=null && !iconUrl.equals("")){
                String path=PictureUtil.getIconPath(this);
                PictureUtil.downloadImage(iconUrl,imageFilename,this,imageView);
            }
        }
    }

    //点击事件
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), InputActivity.class);
            switch (view.getId()) {
                case R.id.activity_setuserinfo_seticon:
                    photoPopupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                case R.id.activity_setuserinfo_setname:
                    intent.putExtra("title", "修改姓名");
                    intent.putExtra("content",userBean.getName());
                    startActivityForResult(intent, REQUEST_NAME_GET);
                    break;
                case R.id.activity_setuserinfo_setemail:
                    intent.putExtra("title", "修改邮箱");
                    intent.putExtra("content", userBean.getEmail());
                    startActivityForResult(intent, REQUEST_EMAIL_GET);
                    break;
                case R.id.activity_setuserinfo_setgender:
                    genderPopupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                case R.id.activity_setuserinfo_setgrade:
                    intent.putExtra("title", "修改年级");
                    intent.putExtra("content", userBean.getGrade());
                    startActivityForResult(intent, REQUEST_GRADE_GET);
                    break;
                case R.id.activity_setuserinfo_setscore:
                    Toast.makeText(getApplicationContext(),"信誉分不可修改",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.activity_setuserinfo_save://保存按钮
                    userBean.setEmail(T_email.getText().toString());
                    userBean.setName(T_name.getText().toString());
                    userBean.setGender(T_gender.getText().toString());
                    userBean.setGrade(T_grade.getText().toString());
                    updateUserInfo(userBean);
                    break;
            }
        }

    };

    //回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 回调成功
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NAME_GET:
                    userBean.setName(data.getStringExtra("input"));
                    T_name.setText(userBean.getName());
                    break;
                case REQUEST_EMAIL_GET:
                    userBean.setName(data.getStringExtra("input"));
                    T_email.setText(userBean.getEmail());
                    break;
                case REQUEST_GENDER_GET:
                    userBean.setGender(data.getStringExtra("input"));
                    T_gender.setText(userBean.getGender());

                    break;
                case REQUEST_GRADE_GET:
                    userBean.setGrade(data.getStringExtra("input"));
                    T_grade.setText(userBean.getGrade());
                    break;
                case REQUEST_SMALL_IMAGE_CUTTING:// 裁剪图片返回，设置头像
                    String imageFilename=PictureUtil.getIconPath(SetUserInfoActivity.this);
                    File imageFile=new File(imageFilename);
                    if (imageFile.exists()){
                        getImageURL(imageFile);
                    }else{
                        Toast.makeText(SetUserInfoActivity.this,"图片不存在",Toast.LENGTH_SHORT).show();
                    }

                    PictureUtil.readIconFromFile(getApplicationContext(), imageView);
                    break;
                // 相册选取返回调用，裁剪图片
                case REQUEST_IMAGE_GET:
                    PictureUtil.cropphoto(this, data.getData());
                    break;
                // 拍照
                case REQUEST_IMAGE_CAPTURE:
//                    File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//                            +File.separator+"user_icon"+File.separator
//                            +AccountUtil.getAccount(SetUserInfoActivity.this)+".jpg");
                    String filename=PictureUtil.getPicturePath(SetUserInfoActivity.this)+File.separator
                            +AccountUtil.getAccount(SetUserInfoActivity.this)+".jpg";
                    Log.d("photo", "拍照后回调"+filename);
                    File file=new File(filename);
                    if(file.exists()){
                        Log.d("photo", "拍照后回调"+file.exists());
                    }
                    Uri uri=PictureUtil.getDcimUri(getApplicationContext(), file);
                    Log.d("photo", "拍照后回调"+uri);
                    PictureUtil.cropphoto(SetUserInfoActivity.this, uri);
//                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
//                    startSmallPhotoZoom(Uri.fromFile(temp));
                    break;

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateUserInfo(final UserBean userBean){
        RequestBody requestBody=new FormBody.Builder()
                .add("id",""+AccountUtil.getAccount(SetUserInfoActivity.this))
                .add("email",userBean.getEmail())
                .add("password",userBean.getPassword())
                .add("name",userBean.getName())
                .add("gender",userBean.getGender())
                .add("grade",userBean.getGrade())
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SetUserInfoActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
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
                    //信息成功更新到服务器了才更新本地信息
                    AccountUtil.setUserInfo(SetUserInfoActivity.this,userBean);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SetUserInfoActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/UpdateUserInfo";
//        String url="http://192.168.1.100:8080/ShanPin/UpdateUserInfo";
//        String url="http://172.16.212.200:8080/ShanPin/UpdateUserInfo";
        Okhttp.sentPost(url,requestBody,callback);

    }

    private void getImageURL(File imageFile){
        RequestBody fileBody=RequestBody.Companion.create(imageFile,MediaType.parse("image/*"));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(imageFile.getName(), PictureUtil.changeImageName(imageFile.getName()), fileBody)
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SetUserInfoActivity.this,"上传图片失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText=response.body().string();
                Log.d("PictureUtil", "上传图片: "+responseText);
                updateIcon(responseText);
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/ImageServlet";
        Request request= new Request.Builder()
//                .addHeader("Content-Type","image/*")
//                .addHeader("Conftent-Type","multipart/form-data")
                .url(url).post(requestBody)
                .build();
        Okhttp.getOkHttpClient().newCall(request).enqueue(callback);
    }

//    public void getImageURL(File imageFile){
//        RequestBody fileBody=RequestBody.Companion.create(imageFile,MediaType.parse("image/*"));
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("smfile", PictureUtil.changeImageName(imageFile.getName()), fileBody)
//                .build();
//        Callback callback=new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(SetUserInfoActivity.this,"上传图片失败",Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String responseText=response.body().string();
//                Log.d("PictureUtil", "上传图片: "+responseText);
//                ImageBean imageBean=new Gson().fromJson(responseText,ImageBean.class);
//                updateIcon(imageBean.getData().getUrl());
//
//            }
//        };
//        String url="https://sm.ms/api/v2/upload";
//        Request request= new Request.Builder()
////                .addHeader("Content-Type","image/*")
////                .addHeader("Conftent-Type","multipart/form-data")
//                .addHeader("Authorization","ZMDFAoUIX8Y73ePNfSUQ9eWwk8QMiLwM")
//                .addHeader("user-agent","ShanPin/1.0.0")
//                .url(url).post(requestBody)
//                .build();
//        Okhttp.getOkHttpClient().newCall(request).enqueue(callback);
//    }

    private void updateIcon(String icon){
        RequestBody requestBody=new FormBody.Builder()
                .add("userID",""+AccountUtil.getAccount(SetUserInfoActivity.this))
                .add("icon",icon)
                .build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SetUserInfoActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("login", "onResponse: "+"请检查你的网络");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("login", "onResponse: "+responseText);
                if (responseText.equals("success")){
                    //信息成功更新到服务器了才更新本地信息
                    PictureUtil.readIconFromFile(SetUserInfoActivity.this,imageView);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SetUserInfoActivity.this,"上传头像成功",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/UpdateIcon";
//        String url="http://192.168.1.100:8080/ShanPin/UpdateIcon";
//        String url="http://172.16.212.200:8080/ShanPin/UpdateIcon";
        Okhttp.sentPost(url,requestBody,callback);
    }
}
