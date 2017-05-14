package com.ratik.uttam.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ratik.uttam.utils.AlarmHelper;

/**
 * Created by Ratik on 02/03/16.
 */
public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmHelper.setJobSetAlarm2(context);
    }
}
