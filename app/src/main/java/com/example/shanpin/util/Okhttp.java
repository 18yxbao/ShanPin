package com.example.shanpin.util;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Okhttp {
    //获取OkHttpClient对象时返回这个，单例设计
    private volatile static OkHttpClient okHttpClient;
    private static final String url="";

    public Okhttp() {
    }

    //DCL双检查锁机制（DCL：double checked locking）
    public static OkHttpClient getOkHttpClient() {
        // 第一次检查OkHttpClient是否被实例化出来，如果没有进入if块
        if(okHttpClient==null){
            //加synchronized关键字实现同步
            synchronized (Okhttp.class){
                // 某个线程取得了类锁，实例化对象前第二次检查OkHttpClient是否已经被实例化出来，如果没有，才最终实例出对象
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient();
                }
            }
        }
        return okHttpClient;
    }

//    public static RequestBody getRequestBody(){
//        return RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"));
//    }

    //通过post的方式发送异步请求
    public static void sentPost(String url, RequestBody requestBody, Callback callback) {
        OkHttpClient okHttpClient = getOkHttpClient();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    //通过post的方式发送同步请求
    public static String sentPost(String url, RequestBody requestBody) {
        String responseData = null;
        OkHttpClient okHttpClient = getOkHttpClient();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            Response response= okHttpClient.newCall(request).execute();
            responseData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }

    //通过get的方式发送异步请求
    public static void sentGet(String url,RequestBody requestBody, Callback callback) {
        OkHttpClient okHttpClient = getOkHttpClient();
        Request request = new Request.Builder().url(url).method("GET",requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    //通过get的方式发送异步请求
    public static void sentGet(String url, Callback callback) {
        OkHttpClient okHttpClient = getOkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
