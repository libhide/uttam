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
        String lorem = "Egg whites, turkey sausage, wheat toast, water. Of course they don’t want " +
                "us to eat our breakfast, so we are going to enjoy our breakfast.";

        Fragment firstSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_1, "Welcome to Uttam!",
                lorem, Color.WHITE, Color.WHITE);

        Fragment secondSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_2, "A new wallpaper notification everyday",
                lorem, Color.WHITE, Color.WHITE);

        Fragment thirdSlide = MaterialSlide.newInstance(R.drawable.tour_graphic_3, "Press the tick to set the wallpaper",
                lorem, Color.WHITE, Color.WHITE);

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
            Toast.makeText(this, "Set a refresh interval first, please!", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(TourActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }
}
