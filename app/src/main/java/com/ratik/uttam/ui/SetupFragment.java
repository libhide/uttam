package com.ratik.uttam.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ratik.uttam.R;
import com.ratik.uttam.utils.PrefUtils;

/**
 * Created by Ratik on 08/03/16.
 */
public class SetupFragment extends Fragment {

    public static Button setupButton;
    private TextView tipView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setup, container, false);

        tipView = (TextView) root.findViewById(R.id.tipTextView);
        setupButton = (Button) root.findViewById(R.id.setupButton);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetupDialog();
                setupButton.setEnabled(false);
            }
        });

        return root;
    }

    private void showSetupDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Refresh Interval");

        String[] intervals = getResources().getStringArray(R.array.intervals);
        ArrayAdapter<String> intervalsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, intervals);
        builder.setAdapter(intervalsAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // DAILY
                        PrefUtils.setRefreshInterval(getActivity(), "daily");
                        break;
                    case 1:
                        // WEEKLY
                        PrefUtils.setRefreshInterval(getActivity(), "weekly");
                        break;
                }
                tipView.setVisibility(View.VISIBLE);
                setupButton.setText("All set!");
                setupButton.setEnabled(false);
            }
        });

        builder.create().show();
    }
}