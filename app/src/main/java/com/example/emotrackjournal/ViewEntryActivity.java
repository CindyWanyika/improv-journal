package com.example.emotrackjournal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

public class ViewEntryActivity extends AppCompatActivity {

    private TextView viewEntryDate, viewEntryMood, viewEntryTitle, viewEntryNote;
    private Button backButton, editButton;
    private FirebaseFirestore db;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        viewEntryDate = findViewById(R.id.viewEntryDate);
        viewEntryMood = findViewById(R.id.viewEntryMood);
        viewEntryTitle = findViewById(R.id.viewEntryTitle);
        viewEntryNote = findViewById(R.id.viewEntryNote);
        backButton = findViewById(R.id.backButton);
        editButton = findViewById(R.id.editButton);

        db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String selectedDate = getIntent().getStringExtra("selectedDate");

        viewEntryDate.setText(selectedDate);

        // 🔍 Fetch entry for selected date
        db.collection("users").document(uid)
                .collection("entries")
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        DocumentSnapshot doc = query.getDocuments().get(0);
                        documentId = doc.getId(); // Save ID for editing
                        viewEntryMood.setText("Mood: " + doc.getString("mood"));
                        viewEntryTitle.setText(doc.getString("title"));
                        viewEntryNote.setText(doc.getString("note"));
                    } else {
                        Toast.makeText(this, "No entry found for this date", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load entry", Toast.LENGTH_SHORT).show()
                );

        // 🔙 Back to main
        backButton.setOnClickListener(v -> finish());

        // ✏️ Edit
       /* editButton.setOnClickListener(v -> {
            Intent editIntent = new Intent(this, EditEntryActivity.class);
            editIntent.putExtra("documentId", documentId);
            editIntent.putExtra("date", selectedDate);
            startActivity(editIntent);
        });*/
    }
}
