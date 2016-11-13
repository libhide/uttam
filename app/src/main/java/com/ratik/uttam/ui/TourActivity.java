package com.ratik.uttam.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

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

        String first = getString(R.string.tour_slide_1_text);
        String second = getString(R.string.tour_slide_2_text);
        String third = getString(R.string.tour_slide_3_text);

        Fragment firstSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_1, getString(R.string.tour_slide_1_heading),
                first, Color.WHITE, Color.WHITE);

        Fragment secondSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_2, getString(R.string.tour_slide_2_heading),
                second, Color.WHITE, Color.WHITE);

        Fragment thirdSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_3, getString(R.string.tour_slide_3_heading),
                third, Color.WHITE, Color.WHITE);

        addSlide(firstSlide, slideColor);
        addSlide(secondSlide, slideColor);
        addSlide(thirdSlide, slideColor);
        addSlide(new AllDoneFragment(), slideColor);

        // Customize tour
        hideSkip();
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
        startActivity(new Intent(TourActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}
