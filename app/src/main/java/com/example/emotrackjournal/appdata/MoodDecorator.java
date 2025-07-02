package com.example.emotrackjournal.appdata;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashSet;

public class MoodDecorator implements DayViewDecorator {

    private final int color;
    private final HashSet<CalendarDay> dates;

    public MoodDecorator(int color, HashSet<CalendarDay> dates) {
        this.color = color;
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        GradientDrawable circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL);
        circleDrawable.setColor(color);
        circleDrawable.setSize(50, 50);

        view.setBackgroundDrawable(circleDrawable);
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
    }
}

