/*
 * Copyright (C) 2013 FMSoft (http://www.fmsoft.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.espier.clock.broadcast;


import org.espier.clock.Alarms;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public class AlarmInitReceiver extends BroadcastReceiver {

    /**
     * Sets alarm on ACTION_BOOT_COMPLETED. Resets alarm on TIME_SET, TIMEZONE_CHANGED
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // if (Log.LOGV) Log.v("AlarmInitReceiver" + action);

        if (context.getContentResolver() == null) {
            // Log.e("AlarmInitReceiver: FAILURE unable to get content resolver.  Alarms inactive.");
            return;
        }
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            System.out.println("ddddddddddd");
            Alarms.saveSnoozeAlert(context, -1, -1, false);
            Alarms.disableExpiredAlarms(context);
            // Log.v("ACTION_BOOT_COMPLETED\t"+Intent.ACTION_BOOT_COMPLETED);
        }
        Alarms.setNextAlert(context, false);
        System.out.println("ddddddddddd");
    }
}
