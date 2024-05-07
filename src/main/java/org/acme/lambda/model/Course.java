package org.acme.lambda.model;

public class Course {
    String id;
    String title;
    String author;


    public Course(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Course() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
