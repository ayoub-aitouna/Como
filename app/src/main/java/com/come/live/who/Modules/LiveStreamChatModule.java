package com.come.live.who.Modules;

public class LiveStreamChatModule {
    String Username, Message, channelId;
    int Gift, SenderId;

    public LiveStreamChatModule() {
    }

    public LiveStreamChatModule(String username, String message, String channelId, int gift, int senderId) {
        Username = username;
        Message = message;
        this.channelId = channelId;
        Gift = gift;
        SenderId = senderId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getGift() {
        return Gift;
    }

    public void setGift(int gift) {
        Gift = gift;
    }

    public int getSenderId() {
        return SenderId;
    }

    public void setSenderId(int senderId) {
        SenderId = senderId;
    }
}
