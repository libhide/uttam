package com.ratik.uttam.ui;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.iap.utils.IabHelper;
import com.ratik.uttam.iap.utils.IabResult;
import com.ratik.uttam.iap.utils.Purchase;
import com.ratik.uttam.utils.Utils;

/**
 * Created by Ratik on 08/03/16.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    // IAP
    private IabHelper iabHelper;
    private Preference removeAdsPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        // Remove Ads IAP
        String base64EncodedPublicKey = getString(R.string.playstore_public_key);
        iabHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
            }
        });

        removeAdsPreference = findPreference(getString(R.string.key_remove_ads));

        if (Utils.haveAdsBeenRemoved(getActivity())) {
            // User has remove ads
            removeAdsPreference.setEnabled(false);
        } else {
            removeAdsPreference.setEnabled(true);
        }

        removeAdsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    iabHelper.launchPurchaseFlow(getActivity(), Constants.SKU_REMOVE_ADS, 1, new IabHelper.OnIabPurchaseFinishedListener() {
                        @Override
                        public void onIabPurchaseFinished(IabResult result, Purchase info) {
                            if (result.isFailure()) {
                                Log.d(TAG, "Error purchasing: " + result);
                            }
                            else if (info.getSku().equals(Constants.SKU_REMOVE_ADS)) {
                                // Add remove ads SharedPreference
                                Utils.setRemoveAdsState(getActivity(), true);
                            }
                        }
                    }, "");
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.e(TAG, "There was an error purchasing the remove ads IAP. Message: " +
                            e.getMessage());
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // For all (most) preferences, attach an OnPreferenceChangeListener
        // so the UI summary can be updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.key_refresh_interval)));
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iabHelper != null) {
            try {
                iabHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.d(TAG, "There was an error disposing the IabHelper");
            }
        }
        iabHelper = null;
    }
}
