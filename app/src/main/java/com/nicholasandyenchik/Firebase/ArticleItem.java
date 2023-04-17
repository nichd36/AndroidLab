package com.nicholasandyenchik.Firebase;

public class ArticleItem {
    private String title;
    private String content;
    private String image;
    private int id;

    public ArticleItem() {
    }

    public ArticleItem(String title, String content, String image, int id) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
