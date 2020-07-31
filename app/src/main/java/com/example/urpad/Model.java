package com.example.urpad;

public class Model {
    private int img;

    public Model(int img, String title, String des) {
        this.img = img;
        this.title = title;
        this.desc=des;
    }

    private String title;
    private String desc;

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
