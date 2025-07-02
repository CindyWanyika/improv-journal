package com.example.emotrackjournal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AccountActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return;

        uid = user.getUid();

        loadUserProfile();
        loadMoodStats();
        loadRecentEntries();
    }

    private void loadUserProfile() {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        ((TextView) findViewById(R.id.profileName)).setText(doc.getString("username"));
                        ((TextView) findViewById(R.id.joinedText)).setText("Journaling since " + doc.getString("dateJoined"));
                    }
                });
    }

    private void loadMoodStats() {
        db.collection("users").document(uid).collection("entries")
                .get()
                .addOnSuccessListener(query -> {
                    int total = query.size();
                    int thisMonth = 0;
                    int happy = 0, sad = 0, angry = 0, excited = 0, neutral = 0;

                    String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Calendar.getInstance().getTime());

                    for (DocumentSnapshot doc : query) {
                        String mood = doc.getString("mood");
                        String date = doc.getString("date");

                        if (date != null && date.contains(currentMonth)) thisMonth++;

                        switch (mood.toLowerCase()) {
                            case "happy": happy++; break;
                            case "sad": sad++; break;
                            case "angry": angry++; break;
                            case "excited": excited++; break;
                            case "neutral": neutral++; break;
                        }
                    }

                    ((TextView) findViewById(R.id.totalEntriesText)).setText("Total Entries\n" + total);
                    ((TextView) findViewById(R.id.monthEntriesText)).setText("This Month\n" + thisMonth);

                    updateProgressBar(R.id.happyProgress, happy, total);
                    updateProgressBar(R.id.sadProgress, sad, total);
                    updateProgressBar(R.id.angryProgress, angry, total);
                    updateProgressBar(R.id.excitedProgress, excited, total);
                    updateProgressBar(R.id.neutralProgress, neutral, total);
                });
    }

    private void loadRecentEntries() {
        db.collection("users").document(uid).collection("entries")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(query -> {
                    LinearLayout container = findViewById(R.id.recentActivityContainer);
                    LayoutInflater inflater = LayoutInflater.from(this);

                    for (DocumentSnapshot doc : query) {
                        String title = doc.getString("title");
                        String date = doc.getString("date");
                        String mood = doc.getString("mood");

                        View item = inflater.inflate(R.layout.recent_entry_item, container, false);
                        ((TextView) item.findViewById(R.id.entryTitle)).setText(title);
                        ((TextView) item.findViewById(R.id.entryDate)).setText(date);
                        item.findViewById(R.id.moodDot).getBackground().setTint(getMoodColor(mood));

                        container.addView(item);
                    }
                });
    }

    private void updateProgressBar(int id, int value, int total) {
        ProgressBar bar = findViewById(id);
        int percent = (total == 0) ? 0 : (int) ((value * 100.0f) / total);
        bar.setProgress(percent);
    }

    private int getMoodColor(String mood) {
        switch (mood.toLowerCase()) {
            case "happy": return 0xFFFBC02D;
            case "sad": return 0xFF2196F3;
            case "angry": return 0xFFF44336;
            case "neutral": return 0xFF4CAF50;
            case "excited": return 0xFFBA68C8;
            default: return 0xFF9E9E9E;
        }
    }
}
