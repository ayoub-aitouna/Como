package com.come.live.who.Modules.Stories;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Stories implements Parcelable {
    @SerializedName("idUser")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("profileImage")
    String img;
    @SerializedName("Content")
    ArrayList<StoriesContent> Content;

    public Stories() {
    }

    public Stories(int id, String name, String img, ArrayList<StoriesContent> content) {
        this.id = id;
        this.name = name;
        this.img = img;
        Content = content;
    }

    protected Stories(Parcel in) {
        name = in.readString();
        img = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static final Creator<Stories> CREATOR = new Creator<Stories>() {
        @Override
        public Stories createFromParcel(Parcel in) {
            return new Stories(in);
        }

        @Override
        public Stories[] newArray(int size) {
            return new Stories[size];
        }
    };

    public ArrayList<StoriesContent> getContent() {
        return Content;
    }

    public void setContent(ArrayList<StoriesContent> content) {
        Content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(img);
    }
}
