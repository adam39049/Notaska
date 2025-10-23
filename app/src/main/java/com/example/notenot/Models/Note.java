package com.example.notenot.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.notenot.Database.DateConverter;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "notes")
@TypeConverters(DateConverter.class)
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String noteText;
    private String tag;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Note() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Note(String noteText, String tag) {
        this.noteText = noteText;
        this.tag = tag;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) {
        this.noteText = noteText;
        this.updatedAt = new Date();
    }

    public String getTag() { return tag; }
    public void setTag(String tag) {
        this.tag = tag;
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}