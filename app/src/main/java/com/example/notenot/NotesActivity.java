package com.example.notenot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notenot.Adapters.NotesAdapter;
import com.example.notenot.Database.RoomDB;
import com.example.notenot.Models.Note;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends BaseActivity {

    private RecyclerView recyclerViewNotes;
    private NotesAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private RoomDB database;
    private ImageButton btnBack, btnAddNote;
    private SearchView searchView;
    private Spinner spinnerTags;
    private TextView tvEmptyNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        hideActionBar();
        initializeSoundSystem();
        initializeViews();
        setupDatabase();
        setupRecyclerView();
        setupClickListeners();
        setupSearchAndFilter();
        loadNotes();
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
            Log.e("NotesActivity", "Error initializing sound: " + e.getMessage());
        }
    }

    private void initializeViews() {
        recyclerViewNotes = findViewById(R.id.recyclerView_notes);
        btnBack = findViewById(R.id.btn_back);
        btnAddNote = findViewById(R.id.btn_add_note);
        searchView = findViewById(R.id.searchView_notes);
        spinnerTags = findViewById(R.id.spinner_tags);
        tvEmptyNotes = findViewById(R.id.tv_empty_notes);
    }

    private void setupDatabase() {
        database = RoomDB.getInstance(this);
    }

    private void setupRecyclerView() {
        adapter = new NotesAdapter(this, this::refreshNotes);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotes.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            playSoundSafely();
            finish();
        });

        btnAddNote.setOnClickListener(v -> {
            playSoundSafely();
            openAddNoteDialog();
        });
    }

    private void setupSearchAndFilter() {
        // إعداد البحث
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                playSoundSafely();
                filterNotes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotes(newText);
                return true;
            }
        });

        // إعداد تصفية العلامات
        setupTagsFilter();
    }

    private void setupTagsFilter() {
        List<String> tags = database.mainDAO().getAllTags();
        tags.add(0, "جميع العلامات"); // إضافة خيار افتراضي

        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tags
        );
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTags.setAdapter(tagAdapter);

        spinnerTags.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedTag = tags.get(position);
                    filterByTag(selectedTag);
                } else {
                    loadNotes(); // عرض جميع الملاحظات
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void loadNotes() {
        noteList = database.mainDAO().getAllNotes();
        adapter.setNotes(noteList);
        updateEmptyState();
    }

    private void filterNotes(String query) {
        if (query.isEmpty()) {
            loadNotes();
        } else {
            List<Note> filteredList = database.mainDAO().searchNotes("%" + query + "%");
            adapter.setNotes(filteredList);
            updateEmptyState();
        }
    }

    private void filterByTag(String tag) {
        List<Note> filteredList = database.mainDAO().getNotesByTag(tag);
        adapter.setNotes(filteredList);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmptyNotes.setVisibility(View.VISIBLE);
            recyclerViewNotes.setVisibility(View.GONE);
        } else {
            tvEmptyNotes.setVisibility(View.GONE);
            recyclerViewNotes.setVisibility(View.VISIBLE);
        }
    }

    private void openAddNoteDialog() {
        // TODO: إنشاء dialog لإضافة/تعديل الملاحظات
        // يمكنك استخدام AlertDialog أو نشاط منفصل
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivityForResult(intent, 200);
    }

    protected void refreshNotes() {
        loadNotes();
        setupTagsFilter(); // تحديث قائمة العلامات
    }

    // ✅ دالة آمنة لتشغيل الصوت
    private void playSoundSafely() {
        try {
            SoundHelper.playButtonSound(this);
        } catch (Exception e) {
            Log.e("NotesActivity", "Error playing sound: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            refreshNotes();
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
            Log.e("NotesActivity", "Error releasing sound: " + e.getMessage());
        }
    }
}