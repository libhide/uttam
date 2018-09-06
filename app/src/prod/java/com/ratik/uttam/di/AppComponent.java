package com.ratik.uttam.di;

import com.ratik.uttam.UttamApplication;
import com.ratik.uttam.di.module.ApiModule;
import com.ratik.uttam.di.module.AppContextModule;
import com.ratik.uttam.di.module.BillingModule;
import com.ratik.uttam.di.module.DataModule;
import com.ratik.uttam.di.module.FirebaseModule;
import com.ratik.uttam.di.module.PresenterModule;
import com.ratik.uttam.di.module.SharedPrefsModule;
import com.ratik.uttam.di.module.UtilsModule;
import com.ratik.uttam.network.GetPhotoJob;
import com.ratik.uttam.ui.hero.HeroActivity;
import com.ratik.uttam.ui.main.MainActivity;
import com.ratik.uttam.ui.settings.SettingsActivity;
import com.ratik.uttam.ui.tour.TourActivity;
import com.ratik.uttam.util.NotificationUtils;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Ratik on 17/10/17.
 */

@Singleton
@Component(modules = {AppContextModule.class, PresenterModule.class,
        ApiModule.class, DataModule.class, SharedPrefsModule.class,
        UtilsModule.class, BillingModule.class, FirebaseModule.class})
public interface AppComponent {

    void inject(UttamApplication application);

    void inject(GetPhotoJob job);

    void inject(HeroActivity activity);

    void inject(TourActivity activity);

    void inject(MainActivity activity);

    void inject(SettingsActivity activity);

    void inject(NotificationUtils notificationUtils);
}