package com.example.notenot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends BaseActivity {

    private SwitchCompat switchSound, switchButtonSound;
    private static final String PREFS = "notenot_prefs";
    private static final String KEY_SOUND = "sound_enabled";
    private static final String KEY_BUTTON_SOUND = "button_sound_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // ✅ إزالة شريط العنوان
        hideActionBar();

        // ✅ تهيئة نظام الصوت
        initializeSoundSystem();

        initializeViews();
        setupPreferences();
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
            Log.e("SettingsActivity", "Error initializing sound system: " + e.getMessage());
        }
    }

    private void initializeViews() {
        // زر الرجوع
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            playSoundSafely();
            finish();
        });

        // زر تخصيص الألوان
        Button btnCustomizeTheme = findViewById(R.id.btn_customize_theme);
        btnCustomizeTheme.setOnClickListener(v -> {
            playSoundSafely();
            Intent intent = new Intent(SettingsActivity.this, ThemeActivity.class);
            startActivity(intent);
        });

        switchSound = findViewById(R.id.switch_sound);
        switchButtonSound = findViewById(R.id.switch_button_sound);
    }

    private void setupPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // صوت الإشعارات
        boolean soundEnabled = prefs.getBoolean(KEY_SOUND, true);
        switchSound.setChecked(soundEnabled);

        // صوت الأزرار
        boolean buttonSoundEnabled = prefs.getBoolean(KEY_BUTTON_SOUND, true);
        switchButtonSound.setChecked(buttonSoundEnabled);

        // إعداد مستمعين للتبديل
        setupSwitchListeners();
    }

    private void setupSwitchListeners() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playSoundSafely();
            prefs.edit().putBoolean(KEY_SOUND, isChecked).apply();
            showToast(isChecked ? "تم تفعيل صوت الإشعارات" : "تم إيقاف صوت الإشعارات");
        });

        switchButtonSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playSoundSafely();
            SoundHelper.setButtonSoundEnabled(SettingsActivity.this, isChecked);
            prefs.edit().putBoolean(KEY_BUTTON_SOUND, isChecked).apply();
            showToast(isChecked ? "تم تفعيل صوت الأزرار" : "تم إيقاف صوت الأزرار");
        });
    }

    // ✅ دالة آمنة لتشغيل الصوت
    private void playSoundSafely() {
        try {
            SoundHelper.playButtonSound(this);
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error playing sound: " + e.getMessage());
        }
    }

    // ✅ دالة مساعدة لعرض الرسائل
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideActionBar();

        // ✅ تحديث حالة الأزرار عند العودة للنشاط
        updateSwitchStates();
    }

    private void updateSwitchStates() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        boolean soundEnabled = prefs.getBoolean(KEY_SOUND, true);
        boolean buttonSoundEnabled = prefs.getBoolean(KEY_BUTTON_SOUND, true);

        // ✅ إزالة المستمعين مؤقتاً لتجنب التكرار
        switchSound.setOnCheckedChangeListener(null);
        switchButtonSound.setOnCheckedChangeListener(null);

        // ✅ تحديث القيم
        switchSound.setChecked(soundEnabled);
        switchButtonSound.setChecked(buttonSoundEnabled);

        // ✅ إعادة تعيين المستمعين
        setupSwitchListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            SoundHelper.release();
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error releasing sound system: " + e.getMessage());
        }
    }
}