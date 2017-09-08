package com.ratik.uttam.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.iap.utils.IabHelper;

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

        removeAdsPreference = findPreference(getString(R.string.key_remove_ads));

        // IAP stuff
        String base64EncodedPublicKey = getString(R.string.playstore_public_key);
        if (MainActivity.userHasRemovedAds) {
            // User has remove ads
            removeAdsPreference.setEnabled(false);
        } else {
            // User has NOT remove ads
            removeAdsPreference.setEnabled(true);

            iabHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
            iabHelper.startSetup(result -> {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e(TAG, "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (iabHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful.");
            });

            removeAdsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        iabHelper.launchPurchaseFlow(getActivity(), Constants.SKU_REMOVE_ADS, 1,
                                purchaseFinishedListener, "");
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }

        Preference contactPreference = getPreferenceManager().findPreference(
                getActivity().getString(R.string.key_contact));
        contactPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]
                        {"ratik96@gmail.com"});
                startActivity(emailIntent);
                return true;
            }
        });

        Preference reviewPreference = getPreferenceManager().findPreference(
                getActivity().getString(R.string.key_review));
        reviewPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent();
                i.setData(Uri.parse("market://details?id=com.ratik.uttam.prod"));
                startActivity(i);
                return true;
            }
        });
    }

    // PurchaseFinishedListener
    IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener = (result, info) -> {
        if (result.isFailure()) {
            Log.d(TAG, "Error purchasing: " + result);
            Toast.makeText(getActivity(), "Error purchasing at the moment. Try again later.",
                    Toast.LENGTH_SHORT).show();
        } else if (info.getSku().equals(Constants.SKU_REMOVE_ADS)) {
            // Success
            Toast.makeText(getActivity(), "Purchased!", Toast.LENGTH_SHORT).show();
            // Finish SettingActivity
            getActivity().finish();
        }
    };

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
            // For other preferences, set the summary to the value's
            // simple string representation.
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!iabHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.i(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}
