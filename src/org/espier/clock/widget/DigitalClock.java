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

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;

import org.espier.clock.util.BusinessUtils;

/**
 * Based on android.widget.DigitalClock
 *
 * FIXME: implement separate views for hours/minutes/seconds, so proportional
 * fonts don't shake rendering
 */

public class DigitalClock extends TextView {

    public static final int FMT_STOPWATCH = 0;
    public static final int FMT_TIMER = 1;

    private final static String STOPWATCH_FMTSTR = "mm:ss.";
    private final static String TIMER_FMTSTR = "k:mm:ss";

    private Calendar mCalendar;
    private Runnable mTicker;
    private Handler mHandler;
    private int mFormat;
    private long mBaseTimeInMillis;
    private long mStartTimeInMillis;
    private boolean mTickerStopped;

    public DigitalClock(Context context) {
        super(context);
        initClock(context);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        setFormat(FMT_STOPWATCH);
        mTickerStopped = true;
        mCalendar.setTimeInMillis(mBaseTimeInMillis);
        setText(format(mCalendar));
        setTypeface(BusinessUtils.getTypeface(context.getApplicationContext()));
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = true;
        super.onAttachedToWindow();
        mHandler = new Handler();
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped) return;
                mCalendar.setTimeInMillis(mBaseTimeInMillis + System.currentTimeMillis()
                        - mStartTimeInMillis);
                setText(format(mCalendar));
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (100 - now % 100);
                mHandler.postAtTime(mTicker, next);
            }
        };
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    public void setFormat(int format) {
        switch (format) {
            case FMT_STOPWATCH:
                mFormat = FMT_STOPWATCH;
                break;
            case FMT_TIMER:
                mFormat = FMT_TIMER;
                break;
            default:
                throw new IllegalArgumentException("Unsupported date format: " + format);
        }
    }

    public void start() {
        mTickerStopped = false;
        mStartTimeInMillis = System.currentTimeMillis();
        mTicker.run();
    }

    public void stop() {
        mTickerStopped = true;
        mBaseTimeInMillis += (System.currentTimeMillis() - mStartTimeInMillis);
    }

    public void reset() {
        mBaseTimeInMillis = 0;
        mCalendar.setTimeInMillis(mBaseTimeInMillis);
        setText(format(mCalendar));
    }

    private CharSequence format(Calendar date) {
        if (mFormat == FMT_STOPWATCH) {
            return DateFormat.format(STOPWATCH_FMTSTR, date).toString()
                    + date.get(Calendar.MILLISECOND) / 100;
        }
        return DateFormat.format(TIMER_FMTSTR, date);
    }

}
