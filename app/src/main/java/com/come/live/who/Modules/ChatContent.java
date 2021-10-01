package com.come.live.who.Modules;


public class ChatContent {
    int id, giftCoins, SenderId, receiverId, Delete_id;
    String contentImage, contentAudio, contentText, Hash_id;
    String readTime, sendTime;
    int CallsDuration;

    public ChatContent() {
    }

    public ChatContent(int id, int giftCoins, int senderId, int receiverId, int delete_id,
                       String contentImage, String contentAudio, String contentText, String hash_id,
                       String readTime, String sendTime, int callsDuration) {
        this.id = id;
        this.giftCoins = giftCoins;
        SenderId = senderId;
        this.receiverId = receiverId;
        Delete_id = delete_id;
        this.contentImage = contentImage;
        this.contentAudio = contentAudio;
        this.contentText = contentText;
        Hash_id = hash_id;
        this.readTime = readTime;
        this.sendTime = sendTime;
        CallsDuration = callsDuration;
    }

    public int getCallsDuration() {
        return CallsDuration;
    }

    public void setCallsDuration(int callsDuration) {
        CallsDuration = callsDuration;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGiftCoins() {
        return giftCoins;
    }

    public void setGiftCoins(int giftCoins) {
        this.giftCoins = giftCoins;
    }

    public int getSenderId() {
        return SenderId;
    }

    public void setSenderId(int senderId) {
        SenderId = senderId;
    }

    public int getDelete_id() {
        return Delete_id;
    }

    public void setDelete_id(int delete_id) {
        Delete_id = delete_id;
    }

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public String getContentAudio() {
        return contentAudio;
    }

    public void setContentAudio(String contentAudio) {
        this.contentAudio = contentAudio;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getHash_id() {
        return Hash_id;
    }

    public void setHash_id(String hash_id) {
        Hash_id = hash_id;
    }

    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
