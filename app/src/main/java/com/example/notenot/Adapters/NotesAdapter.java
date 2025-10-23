package com.example.notenot.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notenot.Database.RoomDB;
import com.example.notenot.Models.Note;
import com.example.notenot.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteVH> {

    private final Context context;
    private List<Note> notes = new ArrayList<>();
    private final OnDataChangedListener onDataChangedListener;

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    public NotesAdapter(Context context, OnDataChangedListener listener) {
        this.context = context;
        this.onDataChangedListener = listener;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NoteVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteVH holder, int position) {
        Note note = notes.get(position);

        holder.tvNoteText.setText(note.getNoteText());

        // عرض العلامة إذا كانت موجودة
        if (note.getTag() != null && !note.getTag().isEmpty()) {
            holder.tvNoteTag.setText(note.getTag());
            holder.tvNoteTag.setVisibility(View.VISIBLE);

            // تلوين العلامات حسب النوع
            setTagColor(holder.tvNoteTag, note.getTag());
        } else {
            holder.tvNoteTag.setVisibility(View.GONE);
        }

        // عرض الوقت المنقضي
        holder.tvNoteTime.setText(getTimeAgo(note.getUpdatedAt()));

        // زر التعديل
        holder.btnEdit.setOnClickListener(v -> {
            // TODO: فتح dialog التعديل
        });

        // زر الحذف
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("حذف الملاحظة")
                    .setMessage("هل تريد حذف هذه الملاحظة؟")
                    .setPositiveButton("حذف", (dialog, which) -> {
                        RoomDB.getInstance(context).mainDAO().delete(note);
                        if (onDataChangedListener != null) onDataChangedListener.onDataChanged();
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        });
    }

    private void setTagColor(TextView tagView, String tag) {
        int color = getTagColor(tag);
        tagView.setBackgroundColor(color);
    }

    private int getTagColor(String tag) {
        // تلوين العلامات حسب النوع
        switch (tag.toLowerCase()) {
            case "مهم":
                return context.getResources().getColor(android.R.color.holo_red_light);
            case "عمل":
                return context.getResources().getColor(android.R.color.holo_blue_light);
            case "شخصي":
                return context.getResources().getColor(android.R.color.holo_green_light);
            case "فكرة":
                return context.getResources().getColor(android.R.color.holo_orange_light);
            default:
                return context.getResources().getColor(android.R.color.holo_purple);
        }
    }

    private String getTimeAgo(Date date) {
        long diff = new Date().getTime() - date.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return "منذ " + days + " يوم";
        } else if (hours > 0) {
            return "منذ " + hours + " ساعة";
        } else if (minutes > 0) {
            return "منذ " + minutes + " دقيقة";
        } else {
            return "الآن";
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteVH extends RecyclerView.ViewHolder {
        TextView tvNoteTag, tvNoteText, tvNoteTime;
        ImageButton btnEdit, btnDelete;

        public NoteVH(@NonNull View itemView) {
            super(itemView);
            tvNoteTag = itemView.findViewById(R.id.tv_note_tag);
            tvNoteText = itemView.findViewById(R.id.tv_note_text);
            tvNoteTime = itemView.findViewById(R.id.tv_note_time);
            btnEdit = itemView.findViewById(R.id.btn_edit_note);
            btnDelete = itemView.findViewById(R.id.btn_delete_note);
        }
    }
}