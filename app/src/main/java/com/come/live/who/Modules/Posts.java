package com.come.live.who.Modules;

public class Posts {
    int id,publisherId;
    String ContentImg;

    public Posts() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public String getContentImg() {
        return ContentImg;
    }

    public void setContentImg(String contentImg) {
        ContentImg = contentImg;
    }
}
