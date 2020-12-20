package com.example.shanpin.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.example.shanpin.bean.UserBean;

import java.io.File;

public class AccountUtil {
    private Boolean isLog;
    private String account;
    private String email;
    private String password;
    private String name;
    private String icon;
    private String gender;
    private String grade;
    private double score;

    public static Boolean getLog(Context context){
        SharedPreferences sprfMain= context.getSharedPreferences("logindate",context.MODE_PRIVATE);
        Boolean isLog=sprfMain.getBoolean("islogin",false);
        sprfMain.edit();
        return isLog;
    }

    public static void setLog(Context context,Boolean log) {
        SharedPreferences.Editor editor = context.getSharedPreferences("logindate", context.MODE_PRIVATE).edit();
        editor.putBoolean("islogin", log);
        editor.apply();
    }


    public static String getAccount(Context context){
        SharedPreferences sprfMain= context.getSharedPreferences("logindate",context.MODE_PRIVATE);
        String account= sprfMain.getString("account","-1");
        sprfMain.edit();
        return account;
    }
    public static void setAccount(Context context,String account) {
        SharedPreferences.Editor editor = context.getSharedPreferences("logindate", context.MODE_PRIVATE).edit();
        editor.putString("account", account);
        editor.apply();
    }


    public static void setUserInfo(Context context, UserBean userBean){

        String account=getAccount(context);
        SharedPreferences.Editor editor = context.getSharedPreferences(account, context.MODE_PRIVATE).edit();
        editor.putString("email", userBean.getEmail());
        editor.putString("password",userBean.getPassword());
        editor.putString("name", userBean.getName());
        editor.putString("icon", userBean.getIcon());
        editor.putString("gender", userBean.getGender());
        editor.putString("grade", userBean.getGrade());
        editor.putFloat("score", (float) userBean.getScore());
        editor.apply();

    }

    public static UserBean getUserInfo(Context context) {
        UserBean userBean=new UserBean();
        String account=getAccount(context);
        SharedPreferences sprfMain= context.getSharedPreferences(account,context.MODE_PRIVATE);
        userBean.setName(sprfMain.getString("name",""));
        userBean.setEmail(sprfMain.getString("email",""));
        userBean.setPassword(sprfMain.getString("password",""));
        userBean.setIcon(sprfMain.getString("icon",""));
        userBean.setGender(sprfMain.getString("gender",""));
        userBean.setGrade(sprfMain.getString("grade",""));
        userBean.setScore(sprfMain.getFloat("score",5));
        sprfMain.edit();
        return userBean;
    }


}
