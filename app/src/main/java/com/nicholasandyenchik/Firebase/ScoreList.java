package com.nicholasandyenchik.Firebase;

public class ScoreList {
    private String id ;
    private String score;

    public ScoreList() {
    }

    public ScoreList(String id, String score) {
        this.id = id;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
