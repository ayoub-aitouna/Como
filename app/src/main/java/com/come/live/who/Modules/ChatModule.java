package com.come.live.who.Modules;

import com.google.gson.annotations.SerializedName;

public class ChatModule {
    String name;
    @SerializedName("profileImage")
    String profileImage;
    String lastMessage,Hash;
    int isAvailable,receiverId,SenderId,seen,unseenNumber;

    public ChatModule() {
    }

    public ChatModule(String name, String profileImage, String lastMessage, String hash, int isAvailable, int receiverId, int senderId, int seen, int unseenNumber) {
        this.name = name;
        this.profileImage = profileImage;
        this.lastMessage = lastMessage;
        Hash = hash;
        this.isAvailable = isAvailable;
        this.receiverId = receiverId;
        SenderId = senderId;
        this.seen = seen;
        this.unseenNumber = unseenNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public int getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(int isAvailable) {
        this.isAvailable = isAvailable;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getSenderId() {
        return SenderId;
    }

    public void setSenderId(int senderId) {
        SenderId = senderId;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public int getUnseenNumber() {
        return unseenNumber;
    }

    public void setUnseenNumber(int unseenNumber) {
        this.unseenNumber = unseenNumber;
    }
}
