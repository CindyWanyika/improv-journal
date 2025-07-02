package com.example.emotrackjournal.appdata;

public class JournalEntry {
    public String date;
    public String title;
    public String content;
    public String mood; // happy/sad/etc

    public JournalEntry() {}

    public JournalEntry(String date, String title, String content, String mood) {
        this.date = date;
        this.title = title;
        this.content = content;
        this.mood = mood;
    }
}

