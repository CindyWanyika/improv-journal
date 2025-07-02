package com.example.emotrackjournal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotrackjournal.appdata.MoodDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashSet;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.example.emotrackjournal.appdata.MoodDecorator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    MaterialCalendarView calendarView;

    ImageButton btnOpenDiary;

    ImageButton accountButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link the calendar view
        calendarView = findViewById(R.id.calendarView);

        // Set the current date selected
        calendarView.setDateSelected(CalendarDay.today(), true);

        loadMoodEntriesFromFirestore();


//        //set a listener for date selection
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            // Build the date string using StringBuilder
            StringBuilder dateBuilder = new StringBuilder();
            String dateStr = dateBuilder
                    .append(date.getYear()).append("-")
                    .append(String.format("%02d", date.getMonth())).append("-")
                    .append(String.format("%02d", date.getDay()))
                    .toString();

            // Pass the date to the view activity
            Intent intent = new Intent(MainActivity.this, ViewEntryActivity.class);
            intent.putExtra("selectedDate", dateStr);
            startActivity(intent);
        });




        btnOpenDiary = findViewById(R.id.btnMoodEntry);

        btnOpenDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start NewEntryActivity
                Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
                startActivity(intent);
            }
        });

        accountButton = findViewById(R.id.accountButton);

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });



    }
    private void loadMoodEntriesFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference entriesRef = db.collection("users").document(uid).collection("entries");

        // Map moods to color sets
        Map<String, HashSet<CalendarDay>> moodDateMap = new HashMap<>();

        entriesRef.get().addOnSuccessListener(querySnapshot -> {
            for (QueryDocumentSnapshot doc : querySnapshot) {
                String mood = doc.getString("mood");
                String date = doc.getString("date");

                if (mood == null || date == null) continue;

                String[] parts = date.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]); // CalendarDay is 0-based for months
                int day = Integer.parseInt(parts[2]);

                CalendarDay calendarDay = CalendarDay.from(year, month, day);

                if (!moodDateMap.containsKey(mood)) {
                    moodDateMap.put(mood, new HashSet<>());
                }
                moodDateMap.get(mood).add(calendarDay);
            }

            // Apply decorators based on mood
            for (Map.Entry<String, HashSet<CalendarDay>> entry : moodDateMap.entrySet()) {
                int color = getColorForMood(entry.getKey());
                calendarView.addDecorator(new MoodDecorator(color, entry.getValue()));
            }
        });
    }
    private int getColorForMood(String mood) {
        switch (mood.toLowerCase()) {
            case "happy": return Color.YELLOW;
            case "sad": return Color.BLUE;
            case "angry": return Color.RED;
            case "neutral": return Color.GREEN;
            case "excited": return Color.MAGENTA; // or your defined color
            default: return Color.GRAY;
        }
}
}
