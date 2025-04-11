package com.tdsa1.tdsa1.Document;



import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "documents")
public class DocumentModel {


    @Id
    private String id;

    @Field(type = FieldType.Text)//Full-text search field (analyzed field for search)
    private String title;

    @Field(type = FieldType.Text)//
    private String content;

    @Field(type = FieldType.Keyword)//For exact matching (non-analyzed field).
    private String author;

    @Field(type = FieldType.Date,format = {}, pattern = "yyyy-MM-dd'T'HH:mm")//FieldType.Date: To store date fields.
    private LocalDateTime createdAt;


    @Field(type = FieldType.Text)
    private String filePath;

    @Field(type = FieldType.Keyword)  // Exact match
    private String fileName;         // document.pdf

    @Field(type = FieldType.Keyword)
    private String fileType;         // application/pdf

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

    @Field(type = FieldType.Long)
    private Long fileSize;           // in bytes


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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}