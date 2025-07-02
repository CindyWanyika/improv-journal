package com.example.emotrackjournal.appdata;

public class MoodEntry {
    public String date;
    public String mood;
    public String title;
    public String note;

    public MoodEntry() {} // Required for Firestore

    public MoodEntry(String date, String mood, String title, String note) {
        this.date = date;
        this.mood = mood;
        this.title = title;
        this.note = note;
    }
}

