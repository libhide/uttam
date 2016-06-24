package com.ratik.uttam.utils;

import android.content.Context;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.iap.utils.IabHelper;
import com.ratik.uttam.iap.utils.IabResult;
import com.ratik.uttam.iap.utils.Inventory;

/**
 * Created by Ratik on 25/06/16.
 */
public class AdUtils {
    public static final String TAG = AdUtils.class.getSimpleName();

    public static void setupIAB(IabHelper iabHelper) {
        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "ERROR: " + result.getMessage());
                } else {
                    // All good!
                }
            }
        });
    }

    public static boolean hasUserRemovedAds(Context context) {
        final boolean[] userHasRemovedAds = {false};
        String base64EncodedPublicKey = context.getResources().getString(R.string.playstore_public_key);
        final IabHelper iabHelper = new IabHelper(context, base64EncodedPublicKey);
        try {
            iabHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                    if (result.isFailure()) {
                        // handle error here
                    }
                    else {
                        // does the user have the remove-ads upgrade?
                        if (inv.hasPurchase(Constants.SKU_REMOVE_ADS)) {
                            userHasRemovedAds[0] = true;
                        }
                    }
                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        return userHasRemovedAds[0];
    }
}
