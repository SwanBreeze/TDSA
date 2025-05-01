package com.tdsa1.tdsa1.Document;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BaseDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String author;

    @JsonIgnore
    private LocalDate dateCreation;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd")
    private String dateCreationStr;

    private boolean isPublic;

    @Field(type = FieldType.Text)
    private String filePath;

    private long fileSize;

    @Field(type = FieldType.Text)
    private String content;

    // Getters/Setters

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

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
        if (dateCreation != null) {
            this.dateCreationStr = dateCreation.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    public String getDateCreationStr() {
        return dateCreationStr;
    }

    public void setDateCreationStr(String dateCreationStr) {
        this.dateCreationStr = dateCreationStr;
        if (dateCreationStr != null) {
            this.dateCreation = LocalDate.parse(dateCreationStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}