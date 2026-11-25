package com.example.novella;

public class Book {

    private String id;
    private String title;
    private String author;
    private String description;
    private String genre;
    private double price;
    private String coverUrl;
    private String fileUrl;
    private boolean isAvailable;
    private boolean recommended;

    public Book() { }

    public Book(String id, String title, String author,
                String description, String genre, double price,
                String coverUrl, String fileUrl, boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.price = price;
        this.coverUrl = coverUrl;
        this.fileUrl = fileUrl;
        this.isAvailable = isAvailable;
    }

    public Book(String id, String title, String author, String genre,
                String description, double price, String coverUrl,
                boolean recommended, boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.price = price;
        this.coverUrl = coverUrl;
        this.recommended = recommended;
        this.isAvailable = isAvailable;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }

    public boolean isRecommended() { return recommended; }
    public void setRecommended(boolean recommended) { this.recommended = recommended; }
}