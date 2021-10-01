package com.come.live.who.Modules.Stories;

public class StoriesDashboardModule {
    String Img, name;
    int id;

    public StoriesDashboardModule() {
    }

    public StoriesDashboardModule(String img, String name, int id) {
        Img = img;
        this.name = name;
        this.id = id;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

