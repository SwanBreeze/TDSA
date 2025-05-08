package com.tdsa1.tdsa1.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;


public class DocumenMetaData {

    private String id;

    private String title;

    private String author;
    private String Keyword;
    private String Category;
    private String indexName;
    private String description;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCreation;

    private String content;
    private String type;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getKeyword() {
        return Keyword;
    }

    public void setKeyword(String keyword) {
        Keyword = keyword;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DocumenMetaData(String id, String title, String content, String author, LocalDateTime createdAt) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}