package com.ratik.uttam.ui.tour;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_done, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        return rootView;
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
}