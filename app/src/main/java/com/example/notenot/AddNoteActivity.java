package com.example.notenot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notenot.Database.RoomDB;
import com.example.notenot.Models.Note;

public class AddNoteActivity extends BaseActivity {

    private EditText etNoteText, etNoteTag;
    private Button btnSave, btnCancel;
    private RoomDB database;
    private Note existingNote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        hideActionBar();
        initializeSoundSystem();
        initializeViews();
        setupClickListeners();

        // التحقق إذا كان تعديل ملاحظة موجودة
        if (getIntent().hasExtra("note")) {
            existingNote = (Note) getIntent().getSerializableExtra("note");
            populateNoteData();
        }
    }

    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initializeSoundSystem() {
        try {
            SoundHelper.initialize(this);
        } catch (Exception e) {
            android.util.Log.e("AddNoteActivity", "Error initializing sound: " + e.getMessage());
        }
    }

    private void initializeViews() {
        etNoteText = findViewById(R.id.et_note_text);
        etNoteTag = findViewById(R.id.et_note_tag);
        btnSave = findViewById(R.id.btn_save_note);
        btnCancel = findViewById(R.id.btn_cancel_note);

        database = RoomDB.getInstance(this);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            playSoundSafely();
            saveNote();
        });

        btnCancel.setOnClickListener(v -> {
            playSoundSafely();
            finish();
        });

        // زر لاختيار العلامات الشائعة
        Button btnChooseTag = findViewById(R.id.btn_choose_tag);
        if (btnChooseTag != null) {
            btnChooseTag.setOnClickListener(v -> {
                playSoundSafely();
                showTagSelectionDialog();
            });
        }
    }

    private void populateNoteData() {
        if (existingNote != null) {
            etNoteText.setText(existingNote.getNoteText());
            etNoteTag.setText(existingNote.getTag());
        }
    }

    private void saveNote() {
        String noteText = etNoteText.getText().toString().trim();
        String tag = etNoteTag.getText().toString().trim();

        if (TextUtils.isEmpty(noteText)) {
            etNoteText.setError("يرجى إدخال نص الملاحظة");
            etNoteText.requestFocus();
            return;
        }

        if (existingNote != null) {
            // تحديث الملاحظة الموجودة
            existingNote.setNoteText(noteText);
            existingNote.setTag(tag);
            database.mainDAO().update(existingNote);
            Toast.makeText(this, "تم تعديل الملاحظة بنجاح", Toast.LENGTH_SHORT).show();
        } else {
            // إضافة ملاحظة جديدة
            Note note = new Note(noteText, tag);
            database.mainDAO().insert(note);
            Toast.makeText(this, "تم إضافة الملاحظة بنجاح", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }

    private void showTagSelectionDialog() {
        String[] commonTags = {"مهم", "عمل", "شخصي", "فكرة", "تذكير", "مشروع"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر علامة")
                .setItems(commonTags, (dialog, which) -> {
                    String selectedTag = commonTags[which];
                    etNoteTag.setText(selectedTag);
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void playSoundSafely() {
        try {
            SoundHelper.playButtonSound(this);
        } catch (Exception e) {
            android.util.Log.e("AddNoteActivity", "Error playing sound: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            SoundHelper.release();
        } catch (Exception e) {
            android.util.Log.e("AddNoteActivity", "Error releasing sound: " + e.getMessage());
        }
    }
}