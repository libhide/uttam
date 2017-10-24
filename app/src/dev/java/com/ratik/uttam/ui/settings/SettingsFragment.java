package com.ratik.uttam.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.ratik.uttam.R;

/**
 * Created by Ratik on 08/03/16.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
