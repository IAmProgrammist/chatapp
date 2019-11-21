package com.example.chatapp.listViewStuff;

public class Model {
    String title;
    String desc;
    int image;

    public Model(String title, String desc, int image) {
        this.title = title;
        this.desc = desc;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public int getImage() {
        return image;
    }
}
