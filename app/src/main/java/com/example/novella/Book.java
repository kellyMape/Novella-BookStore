package com.example.novella;

public class Book {

    private String id;
    private String title;
    private String author;
    private String genre;
    private String description;
    private double price;
    private String coverUrl;
    private String pdfUrl;
    private boolean recommended;

    public Book() {

    }

    public Book(String id, String title, String author, String genre,
                String description, double price, String coverUrl,
                String pdfUrl, boolean recommended) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.price = price;
        this.coverUrl = coverUrl;
        this.pdfUrl = pdfUrl;
        this.recommended = recommended;

    }

    public String getId() {return id;}
    public void setId (String id) {this.id = id;}

    public String getTitle() {return title;}
    public void setTitle (String title) {this.title = title;}

    public String getAuthor() {return author;}
    public void setAuthor (String author) {this.author = author;}

    public String getGenre() {return genre;}
    public void setGenre (String gq) {this.genre = genre;}

    public String getDescription() {return description;}
    public void setDescription (String description) {this.description = description;}

    public double getPrice() {return price;}
    public void setPrice (double price) {this.price = price;}

    public String getCoverUrl() {return coverUrl;}
    public void setCoverUrl (String coverUrl) {this.coverUrl = coverUrl;}

    public String getPdfUrl() {return pdfUrl;}
    public void setPdfUrl (String pdfUrl) {this.pdfUrl = pdfUrl;}

    public boolean isRecommended() {return recommended;}
    public void setRecommended(boolean recommended) {this.recommended = recommended;}
}