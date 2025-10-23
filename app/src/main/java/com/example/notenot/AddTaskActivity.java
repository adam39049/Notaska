package com.example.notenot;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.notenot.Database.RoomDB;
import com.example.notenot.Models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends BaseActivity {

    private EditText etTitle, etDescription, etDueDate;
    private RadioGroup rgPriority;
    private Button btnSave, btnCancel;

    private Task existingTask = null;
    private Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // ✅ إزالة شريط العنوان
        hideActionBar();

        // ✅ تهيئة نظام الصوت
        initializeSoundSystem();

        initializeViews();
        setupDatePicker();
        setupClickListeners();

        if (getIntent().hasExtra("task")) {
            existingTask = (Task) getIntent().getSerializableExtra("task");
            populateTaskData();
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
            Log.e("AddTaskActivity", "Error initializing sound: " + e.getMessage());
        }
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        etDueDate = findViewById(R.id.et_due_date);
        rgPriority = findViewById(R.id.rg_priority);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupDatePicker() {
        etDueDate.setOnClickListener(v -> {
            playSoundSafely(); // ✅ صوت عند فتح منتقي التاريخ
            showDatePicker();
        });
        if (existingTask == null) {
            etDueDate.setText(sdfDate.format(new Date()));
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            playSoundSafely();
            saveTask();
        });

        btnCancel.setOnClickListener(v -> {
            playSoundSafely();
            finish();
        });
    }

    // ✅ دالة آمنة لتشغيل الصوت
    private void playSoundSafely() {
        try {
            SoundHelper.playButtonSound(this);
        } catch (Exception e) {
            Log.e("AddTaskActivity", "Error playing sound: " + e.getMessage());
        }
    }

    private void populateTaskData() {
        if (existingTask != null) {
            etTitle.setText(existingTask.getTitle());
            etDescription.setText(existingTask.getDescription());
            etDueDate.setText(existingTask.getDueDate());
            switch (existingTask.getPriority()) {
                case 1: rgPriority.check(R.id.rb_low); break;
                case 2: rgPriority.check(R.id.rb_medium); break;
                case 3: rgPriority.check(R.id.rb_high); break;
            }
        }
    }

    private void showDatePicker() {
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            playSoundSafely(); // ✅ صوت عند اختيار التاريخ
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            etDueDate.setText(sdfDate.format(calendar.getTime()));
        }, y, m, d);

        dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dp.show();
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("يرجى إدخال عنوان المهمة");
            etTitle.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dueDate)) {
            etDueDate.setError("يرجى اختيار تاريخ الاستحقاق");
            etDueDate.requestFocus();
            return;
        }

        int priority = getSelectedPriority();

        if (existingTask != null) {
            existingTask.setTitle(title);
            existingTask.setDescription(description);
            existingTask.setDueDate(dueDate);
            existingTask.setPriority(priority);
            RoomDB.getInstance(this).mainDAO().update(existingTask);
            NotificationHelper.cancelTaskNotification(this, existingTask);
            NotificationHelper.scheduleTaskNotification(this, existingTask);
            Toast.makeText(this, "تم تعديل المهمة بنجاح", Toast.LENGTH_SHORT).show();
        } else {
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate);
            task.setPriority(priority);
            task.setDone(false);
            task.setCreatedAt(sdfDateTime.format(new Date()));
            long rowId = RoomDB.getInstance(this).mainDAO().insert(task);
            Task saved = findInsertedTask(task);
            if (saved != null) {
                NotificationHelper.scheduleTaskNotification(this, saved);
            }
            Toast.makeText(this, "تم إضافة المهمة بنجاح", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }

    private Task findInsertedTask(Task temp) {
        try {
            for (Task t : RoomDB.getInstance(this).mainDAO().getAll()) {
                if (t.getTitle().equals(temp.getTitle()) &&
                        t.getCreatedAt() != null &&
                        t.getCreatedAt().equals(temp.getCreatedAt())) {
                    return t;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private int getSelectedPriority() {
        int id = rgPriority.getCheckedRadioButtonId();
        if (id == R.id.rb_low) return 1;
        if (id == R.id.rb_medium) return 2;
        if (id == R.id.rb_high) return 3;
        return 1;
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
            Log.e("AddTaskActivity", "Error releasing sound: " + e.getMessage());
        }
    }
}