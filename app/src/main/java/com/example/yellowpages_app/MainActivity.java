package com.example.yellowpages_app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private List<NoteModelClass> noteList;
    private NoteAdapter noteAdapter;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }

            // Get new FCM registration token
            String token = task.getResult();

            // Log and toast
            System.out.println("FCM Token: " + token);
        });


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, Signin.class));
            finish();
            return;  // To prevent further execution
        }

        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        noteList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance("https://yellowpages-app-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("notes");

        // Pass context to the adapter
        noteAdapter = new NoteAdapter(this, noteList, databaseReference);
        recycler_view.setAdapter(noteAdapter);

        fetchNotes();

        ImageButton menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_sheet, null);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();

            FrameLayout signout_button = view.findViewById(R.id.signout_button);
            FrameLayout about_button = view.findViewById(R.id.about_button);
            FrameLayout search_button = view.findViewById(R.id.search_button);

            about_button.setOnClickListener(v1 -> {
                FancyToast.makeText(MainActivity.this, "Not implemented yet! :C", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();

            });

            search_button.setOnClickListener(v1 -> {
                FancyToast.makeText(MainActivity.this, "Not implemented yet! :C", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
            });

            signout_button.setOnClickListener(v1 -> {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, Signin.class));
                FancyToast.makeText(MainActivity.this, "Successfully signed out!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                finish();
            });
        });

        FloatingActionButton fab_button = findViewById(R.id.fab_button);
        fab_button.setOnClickListener(v -> showCustomDialog());
    }

    private void showCustomDialog() {
        // Initialize the dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_box);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        FrameLayout create_button = dialog.findViewById(R.id.create_button);
        FrameLayout cancel_button = dialog.findViewById(R.id.cancel_button);
        EditText title = dialog.findViewById(R.id.title);
        EditText description = dialog.findViewById(R.id.description);

        dialog.show();

        create_button.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                String id = databaseReference.push().getKey();
                String email = user.getEmail();
                createNote(id, email, title.getText().toString(), description.getText().toString(), false);
                dialog.dismiss();
            } else {
                FancyToast.makeText(MainActivity.this, "Failed to create note!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        });

        cancel_button.setOnClickListener(v -> dialog.dismiss());
    }

    private void createNote(String id, String email, String title, String description, boolean completed) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        NoteModelClass note = new NoteModelClass(id, email, title, description, completed);
        databaseReference.child(userId).child(id).setValue(note)
                .addOnSuccessListener(aVoid -> FancyToast.makeText(MainActivity.this, "Entry created successfully!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show())
                .addOnFailureListener(e -> FancyToast.makeText(MainActivity.this, "Failed to create entry!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show());
    }

    private void fetchNotes() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userNotesRef = databaseReference.child(userId);

        userNotesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    NoteModelClass note = noteSnapshot.getValue(NoteModelClass.class);
                    noteList.add(note);
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FancyToast.makeText(MainActivity.this, "Failed to load notes!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        });
    }
}