package com.come.live.who.Modules;

public class PaymentModule {
    String title;
    String ItemKey;
    int price, amount;

    public String getItemKey() {
        return ItemKey;
    }

    public void setItemKey(String itemKey) {
        ItemKey = itemKey;
    }

    public PaymentModule() {
    }

    public PaymentModule(String title, String ItemKey, int price, int amount) {
        this.title = title;
        this.ItemKey = ItemKey;
        this.price = price;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
