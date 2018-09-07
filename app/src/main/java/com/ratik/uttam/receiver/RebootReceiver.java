package com.ratik.uttam.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ratik.uttam.di.Injector;
import com.ratik.uttam.util.AlarmHelper;

import javax.inject.Inject;

/**
 * Created by Ratik on 02/03/16.
 */
public class RebootReceiver extends BroadcastReceiver {

    @Inject
    AlarmHelper alarmHelper;

    public RebootReceiver() {
        Injector.getAppComponent().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && (action.equals(Intent.ACTION_BOOT_COMPLETED))) {
            alarmHelper.setJobSetAlarm();
        }

    }
}
