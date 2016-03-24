package com.ratik.uttam.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.ratik.uttam.R;
import com.vlonjatg.android.apptourlibrary.AppTour;
import com.vlonjatg.android.apptourlibrary.MaterialSlide;

/**
 * Created by Ratik on 08/03/16.
 */
public class TourActivity extends AppTour {

    @Override
    public void init(@Nullable Bundle savedInstanceState) {

        int slideColor = Color.parseColor("#3d3d3d");

        String first = "Uttam is a wallpaper app that gently updates your wallpaper with perfect images everyday.";
        String second = "Uttam pushes rich notifications for you to interact with.";
        String third = "You have full control over the setting of the wallpaper. The choice is yours!";

        Fragment firstSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_1, "Welcome to Uttam!",
                first, Color.WHITE, Color.WHITE);

        Fragment secondSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_2, "Wallpaper notifications",
                second, Color.WHITE, Color.WHITE);

        Fragment thirdSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_3, "Full control",
                third, Color.WHITE, Color.WHITE);

        addSlide(firstSlide, slideColor);
        addSlide(secondSlide, slideColor);
        addSlide(thirdSlide, slideColor);
        addSlide(new SetupFragment(), slideColor);

        // Customize tour
        setSkipButtonTextColor(Color.WHITE);
        setNextButtonColorToWhite();
        setDoneButtonTextColor(Color.WHITE);
        setActiveDotColor(Color.BLACK);
    }

    @Override
    public void onSkipPressed() {
        setCurrentSlide(4);
    }

    @Override
    public void onDonePressed() {
        if (SetupFragment.setupButton.isEnabled()) {
            Toast.makeText(this, "Set a refresh interval first", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(TourActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }
}
