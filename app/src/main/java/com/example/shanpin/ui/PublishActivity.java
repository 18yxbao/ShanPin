package com.example.shanpin.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.shanpin.R;
import com.example.shanpin.bean.PinBean;
import com.example.shanpin.util.AccountUtil;
import com.example.shanpin.util.Okhttp;
import com.example.shanpin.util.PictureUtil;
import com.example.shanpin.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PublishActivity extends AppCompatActivity {
    private static final String TAG = "PublishActivity";
    private Toolbar toolbar;
    private LinearLayout setTitle_icon;
    private LinearLayout setTag;
    private LinearLayout setContent;
    private LinearLayout setMemberLimit;
    private LinearLayout setGenderLimit;
    private LinearLayout setTime;
    private ImageView imageView;
    private EditText P_title;
    private Spinner P_tag;
    private EditText P_content;
    private EditText P_memberMax;
    private EditText P_memberMin;
    private RadioGroup P_genderLimit;
    private TextView P_timeEnd;
    private Button P_publish;

    private PinBean pinBean=new PinBean();
//    private View rootView;
    private static final int PICK_PICTURE=100;
    private static final int PICTURE_PERMISSION=200;
    private String imagePath="";
    private String imageUrl="";
    private Map<String,String> map=new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        //toolbar栏
        toolbar = findViewById(R.id.Publish_toolbar);
        toolbar.setTitle("发帖");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        rootView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_publish, null);

        setTitle_icon = (LinearLayout) findViewById(R.id.Publish_setTitle_icon);
        setTag = (LinearLayout) findViewById(R.id.Publish_setTag);
        setContent = (LinearLayout) findViewById(R.id.Publish_setContent);
        setMemberLimit = (LinearLayout) findViewById(R.id.Publish_setMemberLimit);
        setGenderLimit =(LinearLayout) findViewById(R.id.Publish_GenderLimit);
        setTime = (LinearLayout) findViewById(R.id.Publish_setTime);

        imageView = findViewById(R.id.Publish_icon);
        P_title = (EditText) findViewById(R.id.Publish_title);
        P_tag =  findViewById(R.id.activity_publish_spinner);
        P_content = (EditText) findViewById(R.id.Publish_content);
        P_memberMax = (EditText) findViewById(R.id.Publish_Member_Max);
        P_memberMin = (EditText) findViewById(R.id.Publish_Member_Min);
        P_genderLimit = (RadioGroup) findViewById(R.id.Publish_GenderLimit);
        P_timeEnd = findViewById(R.id.Publish_Time_End);
        P_publish = (Button) findViewById(R.id.Publish_publish);

        P_genderLimit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (P_genderLimit.getId()){
                    case 0: pinBean.setGender_limit("0");
                    case 1: pinBean.setGender_limit("1");
                    case 2: pinBean.setGender_limit("2");
                    default:pinBean.setGender_limit("0");
                }
            }
        });

        P_timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                new DatePickerDialog( PublishActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String text = year + "-" + (month + 1) + "-" + dayOfMonth;
                                P_timeEnd.setText(text);
                            }
                        }
                        ,calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 权限还没有授予，进行申请
                    ActivityCompat.requestPermissions(PublishActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PICTURE_PERMISSION); // 申请的 requestCode 为 200
                }else{
                    //打开相册
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_PICTURE); // 打开相册
                }

            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());

                dialog.setTitle("删除图片");
                dialog.setMessage("确定要删除这张图片吗？");
                dialog.setCancelable(false);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageView.setImageResource(R.drawable.ic_input_add);
                        imagePath="";
                        imageUrl="";
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }
        });


        P_publish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //此处不理解，我想把用户设置的照片获取，设置pinBean的picture属性

                pinBean.setTitle(P_title.getText().toString());
                //拼的标签
                pinBean.setTagID((P_tag.getSelectedItemId()+1)+"");
                pinBean.setContent(P_content.getText().toString());
                pinBean.setMember_max(P_memberMax.getText().toString());
                pinBean.setMember_min(P_memberMin.getText().toString());
                //性别限制标签
                pinBean.setGender_limit(P_genderLimit.getCheckedRadioButtonId()+"");

                //拼的发布时间以及截止时间
//                int Year = P_timeEnd.getYear();
//                int Month = P_timeEnd.getMonth();
//                int Day = P_timeEnd.getDayOfMonth();
//                String timeEnd=Year +"-"+Month+"-"+Day+"-";
                String timeEnd=P_timeEnd.getText().toString();
                pinBean.setTime_end(timeEnd);

                //设置发布帖子的时间
                SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());
                String timeStart = formatter.format(curDate);
                pinBean.setTime_start(timeStart);

                if(!map.isEmpty()){
                    map.clear();
                }
                map.put("tagID",pinBean.getTagID());
                map.put("userID",AccountUtil.getAccount(PublishActivity.this));
                map.put("title",pinBean.getTitle());
                map.put("content",pinBean.getContent());
                map.put("time_start",pinBean.getTime_start());
                map.put("time_end",pinBean.getTime_end());
                map.put("member_min",pinBean.getMember_min());
                map.put("member_max",pinBean.getMember_max());
                map.put("gender_limit",pinBean.getGender_limit());
                if (!imagePath.isEmpty()){
                    File file=new File(imagePath);
                    if (file.exists()){
                        getImageURL(file);//拿到图片网址后会自动调用addPin
                    }else {
                        ToastUtil.showShort(PublishActivity.this,"没找到图片");
                    }
                }else {
                    addPin();
                }
                finish();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_PICTURE:
                if(data!=null){
                    Uri uri=data.getData();
                    imagePath=PictureUtil.getFilePathByUri(this,uri);
                    imageUrl="";
                    PictureUtil.readPictureFromFile(this,imageView,imagePath);
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PICTURE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    // 判断系统中是否有处理该 Intent 的 Activity
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, PICK_PICTURE);
                    } else {
                        Toast.makeText(getApplicationContext(), "未找到图片查看器", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }

    private void getImageURL(File imageFile){
        RequestBody fileBody=RequestBody.Companion.create(imageFile, MediaType.parse("image/*"));
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
                        Toast.makeText(getApplicationContext(),"上传图片失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText=response.body().string();
                Log.d("PictureUtil", "上传图片: "+responseText);
                imageUrl=responseText;
                map.put("picture",responseText);
                addPin();
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/ImageServlet";
        Request request= new Request.Builder()
                .url(url).post(requestBody)
                .build();
        Okhttp.getOkHttpClient().newCall(request).enqueue(callback);
    }

    //提示：发送拼之前先看看有没有图片
    private void addPin(){
        Log.d(TAG, "addPin: map="+map.toString());
        FormBody.Builder builder=new FormBody.Builder();
        if(map!=null&& !map.isEmpty()){
            for (String key:map.keySet()) {
                String value=map.get(key);
                if(!(value==null||value.isEmpty())){
                    builder.add(key,value);
                }
            }
        }
        final RequestBody requestBody=builder.build();
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                Toast.makeText(LoginActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PublishActivity.this,"请检查你的网络",Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("login", "onResponse: "+"请检查你的网络");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
//                                Toast.makeText(LoginActivity.this,responseText,Toast.LENGTH_SHORT).show();
                Log.d("Pin", "onResponse: "+responseText);
                if (!(responseText.equals("fail")||responseText.equals(""))){

                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(PublishActivity.this,responseText);
                        }
                    });
                }
            }
        };
        String url="http://119.29.136.236:8080/ShanPin/AddPin";
        Okhttp.sentPost(url,requestBody,callback);
    }

}
