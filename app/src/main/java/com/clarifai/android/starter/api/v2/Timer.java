package com.clarifai.android.starter.api.v2;

import android.os.Handler;

public class Timer {

    private static final int DESC_BY = 1000;
    private Handler handler;
    private Runnable r;
    private int descBy;

    public void initTimer(Runnable runnable) {

        handler = new Handler();

        r = runnable;
        this.descBy = DESC_BY;
    }

    public void stopTimer() {

        if (handler != null && r != null)
            handler.removeCallbacks(r);
    }

    public void startTimer() {

        if (handler != null && r != null)
            handler.postDelayed(r, descBy);
    }

}