package com.example.novella;

public class Review {
    private int rating;
    private String comment;
    private String userName;
    private long timestamp;

    public Review() {
    }

    public Review(int rating, String comment, String userName, long timestamp) {
        this.rating = rating;
        this.comment = comment;
        this.userName = userName;
        this.timestamp = timestamp;
    }
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
