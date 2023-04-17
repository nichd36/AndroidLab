package com.nicholasandyenchik.Firebase;

public class Bookmark {
    private String bookmarked;
    private String id;

    public Bookmark() {
    }

    public Bookmark(String bookmarked, String id) {
        this.bookmarked = bookmarked;
        this.id = id;
    }

    public String getBookmarked() {
        return bookmarked;
    }
    public void setBookmarked(String bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}


