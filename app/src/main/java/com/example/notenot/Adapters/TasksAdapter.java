package com.example.notenot.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notenot.AddTaskActivity;
import com.example.notenot.Database.RoomDB;
import com.example.notenot.Models.Task;
import com.example.notenot.NotificationHelper;
import com.example.notenot.R;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskVH> {

    private final Context context;
    private List<Task> tasks = new ArrayList<>();
    private final OnDataChangedListener onDataChangedListener;

    // واجهة للتحديث عند تعديل المهام
    public interface OnDataChangedListener {
        void onDataChanged();
    }

    public TasksAdapter(Context context, OnDataChangedListener listener) {
        this.context = context;
        this.onDataChangedListener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskVH holder, int position) {
        Task task = tasks.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());
        holder.tvDueDate.setText(task.getDueDate());
        holder.cbStatus.setChecked(task.isDone());

        // مؤشر الأولوية
        if (task.getPriority() == 3) {
            holder.ivPriority.setImageResource(android.R.drawable.ic_dialog_alert);
        } else if (task.getPriority() == 2) {
            holder.ivPriority.setImageResource(android.R.drawable.ic_menu_info_details);
        } else {
            holder.ivPriority.setImageResource(android.R.drawable.checkbox_off_background);
        }

        // عند تغيير حالة المهمة
        holder.cbStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setDone(isChecked);
            RoomDB.getInstance(context).mainDAO().update(task);
            if (onDataChangedListener != null) onDataChangedListener.onDataChanged();
        });

        // عند الضغط على زر التعديل
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTaskActivity.class);
            intent.putExtra("task", task);
            context.startActivity(intent);
        });

        // عند الضغط على زر الحذف
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("حذف المهمة")
                    .setMessage("هل تريد حذف هذه المهمة؟")
                    .setPositiveButton("حذف", (dialog, which) -> {
                        RoomDB.getInstance(context).mainDAO().delete(task);
                        // إلغاء الإشعار إن وجد
                        NotificationHelper.cancelTaskNotification(context, task);
                        if (onDataChangedListener != null) onDataChangedListener.onDataChanged();
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskVH extends RecyclerView.ViewHolder {
        CheckBox cbStatus;
        TextView tvTitle, tvDescription, tvDueDate;
        ImageView ivPriority;
        ImageButton btnEdit, btnDelete;

        public TaskVH(@NonNull View itemView) {
            super(itemView);
            cbStatus = itemView.findViewById(R.id.cb_task_status);
            tvTitle = itemView.findViewById(R.id.tv_task_title);
            tvDescription = itemView.findViewById(R.id.tv_task_description);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            ivPriority = itemView.findViewById(R.id.iv_priority);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
