package com.ratik.uttam.ui;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.ImageView;

import com.ratik.uttam.R;

/**
 * Created by Ratik on 13/11/16.
 */

public class AllDoneFragment extends Fragment {

    private ObjectAnimator animator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_done, container, false);

        ImageView check = (ImageView) root.findViewById(R.id.imageView);

        animator = ObjectAnimator.ofFloat(check, "rotation", -4f, 4f);
        animator.setTarget(check);
        animator.setInterpolator(new AnticipateInterpolator());
        animator.setDuration(500);

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            animator.start();
        }
    }
}