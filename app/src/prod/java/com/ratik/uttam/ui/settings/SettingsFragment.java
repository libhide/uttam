package com.ratik.uttam.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    private Preference removeAdsPreference;
    public static final String ARG_ADS_REMOVED = "adverts_removed";
    private RemoveAdsClickListener removeAdsClickListener;

    public static SettingsFragment newInstance(Bundle args) {
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    interface RemoveAdsClickListener {
        void startRemoveAdsPurchaseFlow();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            removeAdsClickListener = (RemoveAdsClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RemoveAdsClickListener");
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
            removeAdsClickListener.startRemoveAdsPurchaseFlow();
            return true;
        });

        Preference contactPreference = getPreferenceManager().findPreference("contactDev");
        if (contactPreference != null) {
            contactPreference.setOnPreferenceClickListener(preference -> {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ratik96@gmail.com"});
                startActivity(emailIntent);
                return true;
            });
        }

        Preference reviewPreference = getPreferenceManager().findPreference("review");
        if (reviewPreference != null) {
            reviewPreference.setOnPreferenceClickListener(preference -> {
                Intent i = new Intent();
                i.setData(Uri.parse("market://details?id=com.ratik.uttam.prod"));
                startActivity(i);
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