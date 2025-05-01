package com.tdsa1.tdsa1.Document;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(indexName = "documents")
public class DocumentModel {


    @Id
    private String id;

    @Field(type = FieldType.Text)//Full-text search field (analyzed field for search)
    private String title;



    @Field(type = FieldType.Keyword)//For exact matching (non-analyzed field).
    private String author;

    //authentication and authorization

//    @Field(type = FieldType.Boolean )
//    private boolean isPublic;
//
//    @Field(type = FieldType.Keyword)
//    private String  owner;
//
//
//    @Field(type = FieldType.Keyword)
//    private String service;

    @Field(type = FieldType.Date,format = {}, pattern = "yyyy-MM-dd")//FieldType.Date: To store date fields.
    private LocalDate dateCreation;


    @Field(type = FieldType.Text)
    private String filePath;

    @Field(type = FieldType.Keyword)  // Exact match
    private String fileName;         // document.pdf

    @Field(type = FieldType.Keyword)
    private String fileType;
    // application/pdf

    @Field(type = FieldType.Long)
    private Long fileSize;

    @Field(type = FieldType.Text)//
    private String content;




    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    // in bytes


    // Constructors
    public DocumentModel() {}


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

//    public boolean isPublic() {
//        return isPublic;
//    }
//
//    public void setPublic(boolean aPublic) {
//        isPublic = aPublic;
//    }
//
//    public String getService() {
//        return service;
//    }
//
//    public void setService(String service) {
//        this.service = service;
//    }
//
//    public String getOwner() {
//        return owner;
//    }
//
//    public void setOwner(String owner) {
//        this.owner = owner;
//    }
}