package com.example.notenot;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

public class SoundHelper {

    private static final String PREFS_NAME = "notenot_prefs";
    private static final String KEY_BUTTON_SOUND = "button_sound_enabled";

    private static MediaPlayer mediaPlayer;
    private static boolean soundEnabled = true;
    private static boolean initialized = false;

    public static void initialize(Context context) {
        if (initialized) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        soundEnabled = prefs.getBoolean(KEY_BUTTON_SOUND, true);

        initialized = true;
    }

    public static void playButtonSound(Context context) {
        if (!initialized) {
            initialize(context);
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        soundEnabled = prefs.getBoolean(KEY_BUTTON_SOUND, true);

        if (!soundEnabled) return;

        try {
            // استخدام صوت نظامي بسيط
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null && audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                return;
            }

            // تشغيل صوت نظامي بسيط
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(0.3f, 0.3f);
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                mediaPlayer.start();
            }

        } catch (Exception e) {
            // تجاهل الأخطاء لتجنب تعطل التطبيق
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    public static void setButtonSoundEnabled(Context context, boolean enabled) {
        soundEnabled = enabled;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_BUTTON_SOUND, enabled).apply();

        // تشغيل صوت تأكيد عند التفعيل
        if (enabled) {
            playButtonSound(context);
        }
    }

    public static boolean isButtonSoundEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_BUTTON_SOUND, true);
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        initialized = false;
    }
}