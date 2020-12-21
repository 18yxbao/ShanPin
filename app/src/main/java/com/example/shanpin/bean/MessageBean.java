package com.example.shanpin.bean;

import android.os.Message;

public class MessageBean {
    private String PinID;
    private String userID;
    private String userName;
    private String tagID;
    private String title;
    private String content;
    private String picture;
    private String time;
//    private String time_start;
//    private String time_end;
//    private String member_min;
//    private String member_max;
//    private String gender_limit;
//    private String status;

    public MessageBean(String title, String Time,String content){
        this.title = title;
        this.content=content;
        this.time=Time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPinID() {
        return PinID;
    }

    public void setPinID(String pinID) {
        PinID = pinID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time=time;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title=title;
    }
//    public String getTime_start() {
//        return time_start;
//    }
//
//    public void setTime_start(String time_start) {
//        this.time_start = time_start;
//    }
//
//    public String getTime_end() {
//        return time_end;
//    }
//
//    public void setTime_end(String time_end) {
//        this.time_end = time_end;
//    }
//
//    public String getMember_min() {
//        return member_min;
//    }
//
//    public void setMember_min(String member_min) {
//        this.member_min = member_min;
//    }
//
//    public String getMember_max() {
//        return member_max;
//    }
//
//    public void setMember_max(String member_max) {
//        this.member_max = member_max;
//    }
//
//    public String getGender_limit() {
//        return gender_limit;
//    }
//
//    public void setGender_limit(String gender_limit) {
//        this.gender_limit = gender_limit;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }

}
