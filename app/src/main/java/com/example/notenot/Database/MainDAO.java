package com.example.notenot.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notenot.Models.Task;
import com.example.notenot.Models.Note; // ✅ إضافة استيراد Note

import java.util.List;

@Dao
public interface MainDAO {

    // ========== عمليات المهام ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM task ORDER BY id DESC")
    List<Task> getAll();

    @Query("SELECT * FROM task WHERE id = :id LIMIT 1")
    Task getById(int id);

    @Query("SELECT COUNT(*) FROM task")
    int countAll();

    @Query("SELECT COUNT(*) FROM task WHERE done = 1")
    int countCompleted();

    @Query("SELECT COUNT(*) FROM task WHERE done = 0")
    int countPending();

    @Query("SELECT COUNT(*) FROM task WHERE priority = 3")
    int countUrgent();

    // ========== ✅ عمليات الملاحظات ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    List<Note> getAllNotes();

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    Note getNoteById(int id);

    @Query("SELECT * FROM notes WHERE tag LIKE :tag ORDER BY updatedAt DESC")
    List<Note> getNotesByTag(String tag);

    @Query("SELECT * FROM notes WHERE noteText LIKE :query OR tag LIKE :query ORDER BY updatedAt DESC")
    List<Note> searchNotes(String query);

    @Query("SELECT DISTINCT tag FROM notes WHERE tag IS NOT NULL AND tag != ''")
    List<String> getAllTags();

    @Query("SELECT COUNT(*) FROM notes")
    int countNotes();
}