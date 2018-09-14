package com.ratik.uttam.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ratik.uttam.Constants;
import com.ratik.uttam.R;
import com.ratik.uttam.billing.BillingManager;
import com.ratik.uttam.di.Injector;

import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ratik on 07/03/16.
 */
public class SettingsActivity extends AppCompatActivity implements SettingsFragment.Callback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.settings_content)
    FrameLayout settingsContentLayout;

    @BindView(R.id.adView)
    AdView adView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Inject
    Billing billing;
    private boolean addFree = true;

    private BillingManager billingManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        Injector.getAppComponent().inject(this);

        // Add Toolbar
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        billingManager = new BillingManager(this, billing);
        billingManager.loadInventory(products -> {
            final Inventory.Product product = products.get(ProductTypes.IN_APP);
            if (product.isPurchased(Constants.Billing.SKU_REMOVE_ADS)) {
                showSettings();
                return;
            }
            showAd();
            showSettings();
        });
    }

    private void showSettings() {
        Bundle args = new Bundle();
        if (addFree) {
            args.putBoolean(SettingsFragment.ARG_ADS_REMOVED, true);
        } else {
            args.putBoolean(SettingsFragment.ARG_ADS_REMOVED, false);
        }
        SettingsFragment fragment = SettingsFragment.newInstance(args);
        getFragmentManager().beginTransaction().replace(
                R.id.settings_content, fragment).commit();
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showAd() {
        addFree = false;

        settingsContentLayout.setPadding(0, 0, 0, 140);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("AE9739935347E14CA9FDC906502E058F")
                .build();
        adView.loadAd(adRequest);
    }

    private void hideAd() {
        addFree = true;
        adView.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        billingManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        billingManager.stopCheckout();
        super.onDestroy();
    }

    @Override
    public void startRemoveAdsPurchaseFlow() {
        billingManager.startPurchaseFlowForIAP(Constants.Billing.SKU_REMOVE_ADS, new EmptyRequestListener<Purchase>() {
            @Override
            public void onSuccess(@Nonnull Purchase result) {
                hideAd();
                addFree = true;
                showSettings();
            }
        });
    }

    @Override
    public void startContactTheDevFlow() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ratik96@gmail.com"});
        startActivity(emailIntent);
    }

    @Override
    public void startRateTheAppFlow() {
        Intent i = new Intent();
        i.setData(Uri.parse("market://details?id=com.ratik.uttam.prod"));
        startActivity(i);
    }
}

