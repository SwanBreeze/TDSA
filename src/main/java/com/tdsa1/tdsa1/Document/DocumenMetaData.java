package com.tdsa1.tdsa1.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DocumenMetaData {

    private String id;

    private String title;

    private String author;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCreation;

    private String content;

    // private boolean isPublic;

    public DocumenMetaData() {
    }



    public DocumenMetaData(String id, String title, String content, String author, LocalDate dateCreation) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.dateCreation = dateCreation;
        this.content = content;
        //this.isPublic = isPublic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

//    public boolean isPublic() {
//        return isPublic;
//    }
//
//    public void setPublic(boolean aPublic) {
//        isPublic = aPublic;
//    }
}