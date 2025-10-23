package com.example.notenot;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_THEME = "app_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // تطبيق الثيم قبل super.onCreate()
        applyStoredTheme();
        super.onCreate(savedInstanceState);
    }

    private void applyStoredTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentTheme = prefs.getInt(KEY_THEME, 0); // 0 = الإفتراضي

        switch (currentTheme) {
            case 1: // أزرق
                setTheme(R.style.AppTheme_Blue);
                break;
            case 2: // أخضر
                setTheme(R.style.AppTheme_Green);
                break;
            case 3: // برتقالي
                setTheme(R.style.AppTheme_Orange);
                break;
            default: // إفتراضي
                setTheme(R.style.Theme_Notenot);
                break;
        }
    }

    // دالة مساعدة للحصول على الثيم الحالي
    public static int getCurrentTheme(SharedPreferences prefs) {
        return prefs.getInt(KEY_THEME, 0);
    }
}