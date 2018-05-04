package com.ratik.uttam.billing;

import android.app.Activity;
import android.content.Intent;

import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.RequestListener;

import io.reactivex.annotations.NonNull;

public class BillingManager {
    private ActivityCheckout activityCheckout;

    public BillingManager(Activity activity, Billing billing) {
        activityCheckout = ActivityCheckout.forActivity(activity, billing);
        this.startCheckout();
    }

    public void loadInventory(@NonNull Inventory.Callback callback) {
        Inventory.Request request = Inventory.Request.create().loadAllPurchases();
        activityCheckout.loadInventory(request, callback);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityCheckout.onActivityResult(requestCode, resultCode, data);
    }

    public void startPurchaseFlowForIAP(String SKU, RequestListener<Purchase> listener) {
        activityCheckout.startPurchaseFlow(ProductTypes.IN_APP, SKU, null, listener);
    }

    public void startCheckout() {
        activityCheckout.start();
    }

    public void stopCheckout() {
        activityCheckout.stop();
    }
}
