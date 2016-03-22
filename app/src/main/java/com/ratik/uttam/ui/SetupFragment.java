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
import android.widget.Toast;

import com.ratik.uttam.R;
import com.ratik.uttam.utils.Utils;

/**
 * Created by Ratik on 08/03/16.
 */
public class SetupFragment extends Fragment {

    public static Button setupButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setup, container, false);

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
                android.R.layout.simple_list_item_single_choice, intervals);
        builder.setAdapter(intervalsAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // DAILY
                        Utils.setRefreshInterval(getActivity(), "daily");
                        break;
                    case 1:
                        // WEEKLY
                        Utils.setRefreshInterval(getActivity(), "weekly");
                        break;
                }
                Toast.makeText(getActivity(), "Awesome! Go on and hit Done now!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }
}