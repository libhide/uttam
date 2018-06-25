package com.ratik.uttam.ui.tour;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ratik.uttam.R;

/**
 * Created by Ratik on 13/11/16.
 */

public class AllDoneFragment extends Fragment {

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_done, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        return rootView;
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
}