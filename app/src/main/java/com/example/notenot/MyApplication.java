package com.example.notenot;

import android.app.Application;
import android.content.SharedPreferences;

public class MyApplication extends Application {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_THEME = "app_theme";

    @Override
    public void onCreate() {
        super.onCreate();
        applyTheme();
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentTheme = prefs.getInt(KEY_THEME, 0);

        switch (currentTheme) {
            case 1:
                setTheme(R.style.AppTheme_Blue);
                break;
            case 2:
                setTheme(R.style.AppTheme_Green);
                break;
            case 3:
                setTheme(R.style.AppTheme_Orange);
                break;
            default:
                setTheme(R.style.Theme_Notenot);
                break;
        }
    }
}