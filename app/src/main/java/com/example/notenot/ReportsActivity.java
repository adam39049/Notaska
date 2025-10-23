package com.example.notenot;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.notenot.Database.RoomDB;
import com.example.notenot.Models.Task;

import java.util.List;

public class ReportsActivity extends BaseActivity {

    private TextView tvTotalTasks, tvCompletedTasks, tvPendingTasks, tvUrgentTasks, tvCompletionRate;
    private ProgressBar progressCompleted, progressPending;
    private RoomDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // ✅ إزالة شريط العنوان
        hideActionBar();

        // ✅ تهيئة نظام الصوت
        initializeSoundSystem();

        initializeViews();
        setupDatabase();
        loadReportData();
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
            Log.e("ReportsActivity", "Error initializing sound: " + e.getMessage());
        }
    }

    private void initializeViews() {
        // ربط العناصر
        tvTotalTasks = findViewById(R.id.tv_total_tasks);
        tvCompletedTasks = findViewById(R.id.tv_completed_tasks);
        tvPendingTasks = findViewById(R.id.tv_pending_tasks);
        tvUrgentTasks = findViewById(R.id.tv_urgent_tasks);
        tvCompletionRate = findViewById(R.id.tv_completion_rate);
        progressCompleted = findViewById(R.id.progress_completed);
        progressPending = findViewById(R.id.progress_pending);

        // زر الرجوع
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            playSoundSafely();
            finish();
        });
    }

    // ✅ دالة آمنة لتشغيل الصوت
    private void playSoundSafely() {
        try {
            SoundHelper.playButtonSound(this);
        } catch (Exception e) {
            Log.e("ReportsActivity", "Error playing sound: " + e.getMessage());
        }
    }

    private void setupDatabase() {
        database = RoomDB.getInstance(this);
    }

    private void loadReportData() {
        List<Task> allTasks = database.mainDAO().getAll();

        int total = allTasks.size();
        int completed = 0;
        int urgent = 0;

        for (Task task : allTasks) {
            if (task.isDone()) {
                completed++;
            } else if (task.getPriority() == 3) {
                urgent++;
            }
        }

        int pending = total - completed;

        int completedPercent = total > 0 ? (completed * 100 / total) : 0;
        int pendingPercent = total > 0 ? (pending * 100 / total) : 0;

        // عرض الأرقام
        tvTotalTasks.setText(String.valueOf(total));
        tvCompletedTasks.setText(String.valueOf(completed));
        tvPendingTasks.setText(String.valueOf(pending));
        tvUrgentTasks.setText(String.valueOf(urgent));
        tvCompletionRate.setText(completedPercent + "%");

        // تحديث التقدم
        progressCompleted.setProgress(completedPercent);
        progressPending.setProgress(pendingPercent);
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
            Log.e("ReportsActivity", "Error releasing sound: " + e.getMessage());
        }
    }
}