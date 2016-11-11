package com.ratik.uttam.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ratik.uttam.utils.AlarmHelper;

import java.util.Calendar;

/**
 * Created by Ratik on 02/03/16.
 */
public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // trial
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay > 0 && hourOfDay < 7) {
            AlarmHelper.setJobSetAlarm(context, false);
        } else {
            AlarmHelper.setJobSetAlarm(context, true);
        }
    }
}
