package com.come.live.who.Modules;

public class GiftModule {
    String img;
    int id, amount;

    public GiftModule() {
    }

    public GiftModule(String img, int id, int amount) {
        this.img = img;
        this.id = id;
        this.amount = amount;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
