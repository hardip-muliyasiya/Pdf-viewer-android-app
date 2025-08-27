package com.android.jayswaminarayan;

import android.app.Activity;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

public class ProgressBarAnimation extends Animation {
    private Context context;
    private Activity activity;
    private ProgressBar progressBar;
    private float from;
    private float to;

    public ProgressBarAnimation(Context context, ProgressBar progressBar, float from, float to) {
        this.context = context;
        activity = (Activity) context;
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to-from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }
}
