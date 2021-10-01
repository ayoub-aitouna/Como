package com.come.live.who.Modules;

public class DiscoverModule {
    Users users;
    String thumbnail;
    boolean IsOnLive;

    public DiscoverModule(Users users, String thumbnail, boolean isOnLive) {
        this.users = users;
        this.thumbnail = thumbnail;
        IsOnLive = isOnLive;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isOnLive() {
        return IsOnLive;
    }

    public void setOnLive(boolean onLive) {
        IsOnLive = onLive;
    }
}
