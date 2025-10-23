package com.example.notenot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.notenot.Database.RoomDB;
import com.example.notenot.Models.Task;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String EXTRA_TASK_ID = "extra_task_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);
        if (taskId == -1) return;

        // fetch task (to show title/desc)
        Task task = RoomDB.getInstance(context).mainDAO().getById(taskId);
        if (task == null) return;

        // show notification
        NotificationHelper.showTaskNotification(context, task);
    }
}
