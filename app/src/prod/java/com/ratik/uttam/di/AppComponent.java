package com.ratik.uttam.di;

import com.ratik.uttam.UttamApplication;
import com.ratik.uttam.di.module.ApiModule;
import com.ratik.uttam.di.module.AppContextModule;
import com.ratik.uttam.di.module.BillingModule;
import com.ratik.uttam.di.module.DataModule;
import com.ratik.uttam.di.module.PresenterModule;
import com.ratik.uttam.di.module.SharedPrefsModule;
import com.ratik.uttam.di.module.UtilsModule;
import com.ratik.uttam.services.GetPhotoJob;
import com.ratik.uttam.services.RefetchService;
import com.ratik.uttam.ui.hero.HeroActivity;
import com.ratik.uttam.ui.main.MainActivity;
import com.ratik.uttam.ui.settings.SettingsActivity;
import com.ratik.uttam.utils.NotificationUtils;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Ratik on 17/10/17.
 */

@Singleton
@Component(modules = {AppContextModule.class, PresenterModule.class,
           ApiModule.class, DataModule.class, SharedPrefsModule.class,
           UtilsModule.class, BillingModule.class})
public interface AppComponent {

    void inject(UttamApplication application);

    void inject(GetPhotoJob job);
    void inject(RefetchService service);

    void inject(HeroActivity activity);

    void inject(MainActivity activity);

    void inject(SettingsActivity activity);

    void inject(NotificationUtils notificationUtils);
}