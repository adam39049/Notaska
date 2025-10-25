package com.example.notenot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

public class ThemeActivity extends BaseActivity {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_THEME = "app_theme";

    public static final int THEME_BLUE = 1;
    public static final int THEME_GREEN = 2;
    public static final int THEME_ORANGE = 3;
    public static final int THEME_PURPLE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        // ✅ إزالة شريط العنوان
        hideActionBar();

        // ✅ تهيئة نظام الصوت
        initializeSoundSystem();

        initViews();
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
            Log.e("ThemeActivity", "Error initializing sound: " + e.getMessage());
        }
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            playSoundSafely();
            finish();
        });

        // النموذج الأزرق
        CardView cardBlue = findViewById(R.id.card_theme_blue);
        Button btnApplyBlue = findViewById(R.id.btn_apply_blue);
        cardBlue.setOnClickListener(v -> {
            playSoundSafely();
            applyTheme(THEME_BLUE);
        });
        btnApplyBlue.setOnClickListener(v -> {
            playSoundSafely();
            applyTheme(THEME_BLUE);
        });

        // النموذج الأخضر
        CardView cardGreen = findViewById(R.id.card_theme_green);
        Button btnApplyGreen = findViewById(R.id.btn_apply_green);
        cardGreen.setOnClickListener(v -> {
            playSoundSafely();
            applyTheme(THEME_GREEN);
        });
        btnApplyGreen.setOnClickListener(v -> {
            playSoundSafely();
            applyTheme(THEME_GREEN);
        });

        // النموذج البرتقالي
        CardView cardOrange = findViewById(R.id.card_theme_orange);
        Button btnApplyOrange = findViewById(R.id.btn_apply_orange);
        cardOrange.setOnClickListener(v -> {
            playSoundSafely();
            applyTheme(THEME_ORANGE);
        });
        btnApplyOrange.setOnClickListener(v -> {
            playSoundSafely();
            applyTheme(THEME_ORANGE);
        });
    }

    // ✅ دالة آمنة لتشغيل الصوت
    private void playSoundSafely() {
        try {
            SoundHelper.playButtonSound(this);
        } catch (Exception e) {
            Log.e("ThemeActivity", "Error playing sound: " + e.getMessage());
        }
    }

    private void applyTheme(int theme) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, theme).apply();

        String themeName = getThemeName(theme);
        Toast.makeText(this, "تم تطبيق النموذج " + themeName, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private String getThemeName(int theme) {
        switch (theme) {
            case THEME_BLUE: return "الأزرق";
            case THEME_GREEN: return "الأخضر";
            case THEME_ORANGE: return "البرتقالي";
            default: return "الإفتراضي";
        }
    }

    public static int getCurrentTheme(SharedPreferences prefs) {
        return prefs.getInt(KEY_THEME, THEME_PURPLE);
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
            Log.e("ThemeActivity", "Error releasing sound: " + e.getMessage());
        }
    }
}