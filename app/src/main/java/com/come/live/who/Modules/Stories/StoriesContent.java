package com.come.live.who.Modules.Stories;

import com.google.gson.annotations.SerializedName;

public class StoriesContent {
    @SerializedName("mediaUrl")
    String mediaUrl;
    @SerializedName("mimeType")
    String mimeType;


    public StoriesContent() {
    }

    public StoriesContent(String mediaUrl, String mimeType) {
        this.mediaUrl = mediaUrl;
        this.mimeType = mimeType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}

