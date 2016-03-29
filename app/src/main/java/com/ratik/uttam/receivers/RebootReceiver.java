package com.ratik.uttam.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ratik.uttam.utils.AlarmHelper;
import com.ratik.uttam.utils.Utils;

/**
 * Created by Ratik on 02/03/16.
 */
public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utils.isAlarmSet(context)) {
            // Set alarm again
            AlarmHelper.setAlarmPostReboot(context);
        }
    }
}
