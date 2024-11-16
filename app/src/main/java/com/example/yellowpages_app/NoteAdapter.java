package com.example.yellowpages_app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<NoteModelClass> noteList;
    private DatabaseReference databaseReference;
    protected Context context;

    public NoteAdapter(Context context, List<NoteModelClass> noteList, DatabaseReference databaseReference) {
        this.context = context;
        this.noteList = noteList;
        this.databaseReference = databaseReference;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_items, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteModelClass note = noteList.get(position);

        holder.rv_title.setText(note.getTitle());
        holder.rv_description.setText(note.getDescription());

        // Set the checkbox state based on the note's completed status
        holder.checkbox.setOnCheckedChangeListener(null); // Prevent listener from triggering during recycling
        holder.checkbox.setChecked(note.isCompleted());

        applyStrikethrough(holder, note.isCompleted());

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            note.setCompleted(isChecked);
            applyStrikethrough(holder, isChecked);
            // Update the note in the database
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String noteId = note.getId();
            databaseReference.child(userId).child(noteId).setValue(note);
        });

        holder.edit_button.setOnClickListener(v -> {
            showCustomDialogUpdate(note);
        });

        holder.delete_button.setOnClickListener(v -> {
            showCustomDialogDelete(note);
        });

        // Set the click listener to toggle the visibility of the expander layout
        holder.itemView.setOnClickListener(v -> {
            if (holder.expander.getVisibility() == View.GONE) {
                holder.expander.setVisibility(View.VISIBLE);
            } else {
                holder.expander.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    private void applyStrikethrough(NoteViewHolder holder, boolean isStrikethrough) {
        int paintFlags = isStrikethrough ? Paint.STRIKE_THRU_TEXT_FLAG : 0;
        holder.rv_title.setPaintFlags(paintFlags);
        holder.rv_description.setPaintFlags(paintFlags);
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView rv_title, rv_description;
        FrameLayout edit_button, delete_button;
        CheckBox checkbox;
        LinearLayout expander; // Add this line to reference the expander layout

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_title = itemView.findViewById(R.id.rv_title);
            rv_description = itemView.findViewById(R.id.rv_description);
            edit_button = itemView.findViewById(R.id.edit_button);
            delete_button = itemView.findViewById(R.id.delete_button);
            checkbox = itemView.findViewById(R.id.checkbox);
            expander = itemView.findViewById(R.id.expander); // Initialize the expander layout
        }
    }

    private void showCustomDialogUpdate(NoteModelClass note) {
        // Initialize the dialog with the provided context
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_box_update);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        EditText title = dialog.findViewById(R.id.title);
        EditText description = dialog.findViewById(R.id.description);
        FrameLayout update_button = dialog.findViewById(R.id.update_button);
        FrameLayout cancel_button = dialog.findViewById(R.id.cancel_button);

        // Pre-fill the dialog with the note's current title and description
        title.setText(note.getTitle());
        description.setText(note.getDescription());

        dialog.show();

        update_button.setOnClickListener(v -> {
            // Get the current user
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();
            String noteId = note.getId();

            // Update the note with new data
            String newTitle = title.getText().toString();
            String newDescription = description.getText().toString();

            NoteModelClass updatedNote = new NoteModelClass(noteId, user.getEmail(), newTitle, newDescription, note.isCompleted());
            databaseReference.child(userId).child(noteId).setValue(updatedNote);

            dialog.dismiss();
            FancyToast.makeText(context, "Entry updated successfully!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
        });

        cancel_button.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void showCustomDialogDelete(NoteModelClass note) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_box_delete);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        FrameLayout delete_button = dialog.findViewById(R.id.delete_button);
        FrameLayout cancel_button = dialog.findViewById(R.id.cancel_button);
        dialog.show();

        delete_button.setOnClickListener(v -> {
            // Get the current user
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String noteId = note.getId();
            databaseReference.child(userId).child(noteId).removeValue();
            dialog.dismiss();
            FancyToast.makeText(context, "Entry deleted successfully!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
        });

        cancel_button.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }
}
