package com.ratik.uttam.listeners;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ratik.uttam.utils.AnimationUtils;

/**
 * Created by Ratik on 08/03/16.
 */
public class LongPressListener extends GestureDetector.SimpleOnGestureListener {
    private View[] views;

    public LongPressListener(View... views) {
        this.views = views;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        for(View v : views) {
            AnimationUtils.fadeOutView(v);
        }
    }
}
