package com.ratik.uttam.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;

import com.ratik.uttam.R;

/**
 * Created by Ratik on 08/03/16.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    public static final String ARG_ADS_REMOVED = "adverts_removed";

    private Preference removeAdsPreference;
    private Callback callback;

    public static SettingsFragment newInstance(Bundle args) {
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    interface Callback {
        void startRemoveAdsPurchaseFlow();

        void startContactTheDevFlow();

        void startRateTheAppFlow();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SettingsFragment.Callback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        removeAdsPreference = findPreference("removeAds");

        initLayout();

        removeAdsPreference.setOnPreferenceClickListener(preference -> {
            callback.startRemoveAdsPurchaseFlow();
            return true;
        });

        Preference contactPreference = getPreferenceManager().findPreference("contactDev");
        if (contactPreference != null) {
            contactPreference.setOnPreferenceClickListener(preference -> {
                callback.startContactTheDevFlow();
                return true;
            });
        }

        Preference reviewPreference = getPreferenceManager().findPreference("review");
        if (reviewPreference != null) {
            reviewPreference.setOnPreferenceClickListener(preference -> {
                callback.startRateTheAppFlow();
                return true;
            });
        }
    }

    private void initLayout() {
        if (getArguments().getBoolean(ARG_ADS_REMOVED)) {
            removeAdsPreference.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}