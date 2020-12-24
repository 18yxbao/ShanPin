package com.example.shanpin.bean;


/**
 * 这个是群聊界面的ListBean
 */
public class MessageBean {
    private String pinID;
    private String title;

    public String getPinID() {
        return pinID;
    }

    public void setPinID(String pinID) {
        this.pinID = pinID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "pinID='" + pinID + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
