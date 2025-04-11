package com.tdsa1.tdsa1.Model;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

public class DocumentModel {
    @Id
    private String id;
    @Field(type = FieldType.Text, name = "student_name")
    private String studentName;

    @Field(type = FieldType.Text, name = "birth_date")
    private String birthDate;

    @Field(type = FieldType.Text, name = "study_level")
    private String studyLevel;

    @Field(type = FieldType.Text)//Full-text search field (analyzed field for search)
    private String title;

    @Field(type = FieldType.Text)//
    private String content;

    @Field(type = FieldType.Keyword)//For exact matching (non-analyzed field).
    private String author;

    @Field(type = FieldType.Date)//FieldType.Date: To store date fields.
    private LocalDateTime createdAt;

    // Constructors
    public DocumentModel() {}

    public DocumentModel(String id, String title, String content, String author, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public String getBirthDate() {
        return birthDate;
    }
    public String getTitle() {
        return title;
    }
    public String getStudyLevel() {
        return studyLevel;
    }
    public void setStudyLevel(String studyLevel) {
        this.studyLevel = studyLevel;
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
}
