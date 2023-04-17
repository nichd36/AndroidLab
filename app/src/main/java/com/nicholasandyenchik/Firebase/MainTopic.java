package com.nicholasandyenchik.Firebase;

public class MainTopic {
    private String title;
    private String desc;
    private String image;
    private int id;

    public MainTopic() {
    }

    public MainTopic(String title, String desc, String image, int id) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}

