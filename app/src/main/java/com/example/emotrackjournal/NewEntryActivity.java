package com.example.emotrackjournal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emotrackjournal.appdata.MoodEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class NewEntryActivity extends AppCompatActivity {

    private TextView entryDate;
    private EditText entryTitle, entryContent;
    private RadioGroup moodGroup;
    private Button saveButton, cancelButton;

    private String selectedMood = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        // Views
        entryDate = findViewById(R.id.entryDate);
        entryTitle = findViewById(R.id.entryTitle);
        entryContent = findViewById(R.id.entryContent);
        moodGroup = findViewById(R.id.moodGroup);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        SimpleDateFormat storeFormat=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Set current date
        String dateStr = new SimpleDateFormat("EEEE,MMMM d, yyyy", Locale.getDefault()).format(new Date());
        entryDate.setText(dateStr);

        // Handle mood selection
        moodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            if (selected != null) {
                selectedMood = selected.getText().toString();
            }
        });

        // Save button logic
        saveButton.setOnClickListener(v -> {
            String title = entryTitle.getText().toString().trim();
            String note = entryContent.getText().toString().trim();
            String date = storeFormat.format(new Date());

            if (selectedMood.isEmpty()) {
                Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show();
                return;
            }

            if (title.isEmpty() || note.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create mood entry object
            MoodEntry moodEntry = new MoodEntry(date, selectedMood, title, note);

            // Store to Firestore
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .collection("entries")
                    .add(moodEntry)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Entry saved!", Toast.LENGTH_SHORT).show();
                        finish(); // return to calendar
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save entry", Toast.LENGTH_SHORT).show();
                    });
        });

        // Cancel button
        cancelButton.setOnClickListener(v -> finish());
    }
}
