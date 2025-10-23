package com.example.notenot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notenot.Database.RoomDB;
import com.example.notenot.Models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private com.example.notenot.Adapters.TasksAdapter adapter;
    private List<Task> taskList = new ArrayList<>();
    private RoomDB database;
    private FloatingActionButton fabAdd;
    private SearchView searchView;
    private ImageButton btnNotes, btnReports, btnSettings;
    private TextView tvTaskCount, tvEmptyTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ إزالة شريط العنوان
        hideActionBar();

        // ✅ تهيئة نظام الصوت
        initializeSoundSystem();

        initializeViews();
        setupDatabase();
        setupRecyclerView();
        setupClickListeners();
        setupSearch();

        // ✅ تحميل البيانات أول مرة
        loadTasks();
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
            Log.e("MainActivity", "Error initializing sound system: " + e.getMessage());
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fab_add);
        searchView = findViewById(R.id.searchView);
        btnNotes = findViewById(R.id.btn_notes);
        btnReports = findViewById(R.id.btn_reports);
        btnSettings = findViewById(R.id.btn_settings);
        tvTaskCount = findViewById(R.id.tv_task_count);
        tvEmptyTasks = findViewById(R.id.tv_empty_tasks);
    }

    private void setupDatabase() {
        database = RoomDB.getInstance(this);
    }

    private void setupRecyclerView() {
        adapter = new com.example.notenot.Adapters.TasksAdapter(this, this::refreshTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // ✅ زر إضافة مهمة جديدة
        fabAdd.setOnClickListener(v -> {
            playSoundSafely();
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivityForResult(intent, 100);
        });

        // ✅ زر الملاحظات (الجديد)
        btnNotes.setOnClickListener(v -> {
            playSoundSafely();
            Intent intent = new Intent(MainActivity.this, NotesActivity.class);
            startActivity(intent);
        });

        // ✅ زر التقارير
        btnReports.setOnClickListener(v -> {
            playSoundSafely();
            Intent intent = new Intent(MainActivity.this, ReportsActivity.class);
            startActivity(intent);
        });

        // ✅ زر الإعدادات
        btnSettings.setOnClickListener(v -> {
            playSoundSafely();
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                playSoundSafely();
                filterTasks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTasks(newText);
                return true;
            }
        });
    }

    private void loadTasks() {
        taskList = database.mainDAO().getAll();
        adapter.setTasks(taskList);
        updateTaskCount();
        updateEmptyState();
    }

    private void filterTasks(String query) {
        String q = query.toLowerCase().trim();
        List<Task> filteredList = new ArrayList<>();

        for (Task t : database.mainDAO().getAll()) {
            String title = t.getTitle() == null ? "" : t.getTitle().toLowerCase();
            String desc = t.getDescription() == null ? "" : t.getDescription().toLowerCase();

            if (title.contains(q) || desc.contains(q)) {
                filteredList.add(t);
            }
        }

        adapter.setTasks(filteredList);
        updateTaskCount();
        updateEmptyState();
    }

    protected void refreshTasks() {
        loadTasks();
    }

    private void updateTaskCount() {
        int count = adapter.getItemCount();
        String countText = count + " مهمة";
        tvTaskCount.setText(countText);
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmptyTasks.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            tvEmptyTasks.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);
        }
    }

    // ✅ دالة آمنة لتشغيل الصوت
    private void playSoundSafely() {
        try {
            SoundHelper.playButtonSound(this);
        } catch (Exception e) {
            Log.e("MainActivity", "Error playing sound: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            refreshTasks();
            showToast("تم تحديث قائمة المهام");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideActionBar();
        refreshTasks(); // ✅ تحديث البيانات عند العودة للنشاط

        // ✅ التحقق من إعدادات الصوت
        checkSoundSettings();
    }

    private void checkSoundSettings() {
        // يمكنك إضافة تحقق من إعدادات الصوت هنا إذا أردت
        boolean soundEnabled = SoundHelper.isButtonSoundEnabled(this);
        if (!soundEnabled) {
            Log.d("MainActivity", "صوت الأزرار معطل");
        }
    }

    // ✅ دالة مساعدة لعرض الرسائل
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            SoundHelper.release();
        } catch (Exception e) {
            Log.e("MainActivity", "Error releasing sound system: " + e.getMessage());
        }
    }
}