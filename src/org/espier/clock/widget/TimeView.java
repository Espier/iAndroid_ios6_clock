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

package org.espier.clock.widget;

import java.util.Calendar;
import java.util.TimeZone;

import org.espier.clock.util.BusinessUtils;


import android.content.Context;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

public class TimeView extends TextView {

    private Context context;
    private Calendar mCalendar;
    private Handler handler = new Handler();

    private int hour;
    private int minute;
    private int second;
    private String timeZone;

    public TimeView(Context context) {
        super(context);
        this.context = context;
    }

    public TimeView(Context contex, AttributeSet attrs) {
        super(contex, attrs);
    }

    public void init(String timezone) {

        this.timeZone = timezone;

        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        if (timeZone == null) {
            timeZone = TimeZone.getDefault().getID();
        }

        mCalendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        java.text.DateFormat df = DateFormat.getTimeFormat(context);
        df.setTimeZone(TimeZone.getTimeZone(timezone));

        String time = df.format(mCalendar.getTime());
        System.out.println("time :" + time);
        if (BusinessUtils.getSystemTime_12_24(context)) {
            String[] timeArray = time.split(":");
            hour = Integer.parseInt(timeArray[0]);
            minute = Integer.parseInt(timeArray[1]);
        } else {
            hour = mCalendar.get(Calendar.HOUR);
            minute = mCalendar.get(Calendar.MINUTE);
        }

        second = mCalendar.get(Calendar.SECOND);
        handler.postDelayed(runTime, 100);
    }

    /**
     * count down
     */
    private void countDown() {

        if (second != 59) {
            second += 1;
            return;
        }
        if (second == 59 && minute != 59) { // 秒为0
            second = 0;
            minute += 1;
            return;

        } else if (second == 59 && minute == 59 && hour != 23) {
            second = 0;
            minute = 0;
            hour += 1;
            return;

        } else if (second == 59 && minute == 59 && hour == 23) {
            second = 0;
            minute = 0;
            hour = 0;
            return;
        }
    }

    private Runnable runTime = new Runnable() {

        @Override
        public void run() {
            countDown();

            if (hour < 10 && minute < 10) {
                setText("0" + hour + ":" + "0" + minute);
            } else if (hour < 10) {
                setText("0" + hour + ":" + minute);
            } else if (minute < 10) {
                setText(hour + ":" + "0" + minute);
            } else if (second < 10) {
                setText(hour + ":" + minute);
            } else {
                setText(hour + ":" + minute);
            }
            invalidate();
            handler.postDelayed(runTime, 1000);

        }
    };

}
