package com.example.emotrackjournal.appdata;

public class MoodStat {
    public int happyCount, sadCount, angryCount, neutralCount, excitedCount;

    public MoodStat() {}

    public MoodStat(int happy, int sad, int angry, int neutral, int excited) {
        this.happyCount = happy;
        this.sadCount = sad;
        this.angryCount = angry;
        this.neutralCount = neutral;
        this.excitedCount = excited;
    }
}

