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
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import java.io.InputStream;
import java.util.TimeZone;

import org.espier.clock.R;

@RemoteView
public class AnalogClock extends View {
    private Time mCalendar;
    private BitmapDrawable mDialDrawable;
    private BitmapDrawable mHourHandDrawable;
    private BitmapDrawable mMinuteHandDrawable;
    private BitmapDrawable mSecondHandDrawable;
    private Resources r;
    private InputStream is;
    private int mDialWidth;
    private int mDialHeight;
    private boolean mAttached = false;
    private boolean isnight = true;
    private float mHours;
    private float mMinutes;
    private float mSeconds;
    private String time_zone;

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String timeZone) {
        time_zone = timeZone;
    }

    private boolean mChanged;

    private Handler loopHandler = new Handler();

    private boolean isRun = false;

    private void run() {
        loopHandler.post(tickRunnable);
    }

    private Runnable tickRunnable = new Runnable() {
        public void run() {
            postInvalidate();
            loopHandler.postDelayed(tickRunnable, 1000);
        }
    };

    public AnalogClock(Context context) {
        super(context);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    public void init(Context context, String timeZone) {
        this.time_zone = timeZone;

        if (time_zone == null) {
            mCalendar = new Time();
        } else {
            mCalendar = new Time(time_zone);
        }
        time_zone = mCalendar.timezone;

    }


    public void init(Context context) {

        // System.out.println("AnalogClock   .................   3   "+time_zone);
        if (time_zone == null) {
            mCalendar = new Time();
        } else {
            // System.out.println("AnalogClock   .................   3 else   "+time_zone);
            mCalendar = new Time(time_zone);
        }
        time_zone = mCalendar.timezone;

        r = this.getContext().getResources();
        is = null;

        is = r.openRawResource(R.drawable.ic_clock_white);
        mDialDrawable = new BitmapDrawable(is);

        is = r.openRawResource(R.drawable.ic_hour_black);
        mHourHandDrawable = new BitmapDrawable(is);

        is = r.openRawResource(R.drawable.ic_minute_black);
        mMinuteHandDrawable = new BitmapDrawable(is);

        is = r.openRawResource(R.drawable.ic_second_red);
        mSecondHandDrawable = new BitmapDrawable(is);

        mDialWidth = mDialDrawable.getIntrinsicWidth();
        mDialHeight = mDialDrawable.getIntrinsicHeight();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            getContext().registerReceiver(mIntentReceiver, filter, null, loopHandler);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float) heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);
        // setMeasuredDimension(mDial.getWidth(), mDial.getHeight());
        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
    }

    public static final int MEASURED_STATE_TOO_SMALL = 0x01000000;
    public static final int MEASURED_STATE_MASK = 0xff000000;

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result | (childMeasuredState & MEASURED_STATE_MASK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isRun) {
            run();
            isRun = true;
            return;
        }
        onTimeChanged();
        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        // int availableWidth = mDial.getWidth();
        // int availableHeight = mDial.getHeight();
        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        final Drawable dial = mDialDrawable;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        // System.out.println("w :"+w+" h:"+h+" availableWidth  :"+availableWidth+" availableHeight :"+availableHeight);
        boolean scaled = false;

        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale =
                    Math.min((float) availableWidth / (float) w, (float) availableHeight
                            / (float) h);
            canvas.save();

            canvas.scale(scale, scale, x, y);
        }

        if (changed) {
            // System.out.println("dial   "+(x-(w/2))+" "+(y - (h /2))+" "+(x + (w / 2))+" "+(y + (h
            // / 2)));
            // dial.setBounds(x - (w), y - (h), x + (w), y + (h));
            // dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
            dial.setBounds(0, 0, availableWidth, availableHeight);
            // dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + h);
        }
        dial.draw(canvas);

        canvas.save();

        canvas.rotate(mHours / 12.0f * 360.0f, x, y);
        final Drawable hourHand = mHourHandDrawable;
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            // System.out.println("hours   "+(x-(w/2))+" "+(y - (h * 2 / 3))+" "+(x + (w /
            // 2))+" "+(y + (h / 3)));
            // hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
            hourHand.setBounds(0, 0, availableWidth, availableHeight);
            // hourHand.setBounds(x - (w / 2), y - (h * 2 / 3), x + (w / 2), y + (h / 3));
        }
        hourHand.draw(canvas);

        canvas.restore();

        canvas.save();
        canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

        final Drawable minuteHand = mMinuteHandDrawable;
        if (changed) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            // System.out.println("minute   "+(x-(w/2))+" "+(y - (h * 4 / 5))+" "+(x + (w /
            // 2))+" "+(y + (h / 5)));
            // minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
            minuteHand.setBounds(0, 0, availableWidth, availableHeight);
            // minuteHand.setBounds(x - (w / 2), y - (h * 4 / 5), x + (w / 2), y + (h / 5));
        }
        minuteHand.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.rotate(mSeconds / 60.0f * 360.0f, x, y);

        final Drawable scendHand = mSecondHandDrawable;
        if (changed) {
            w = scendHand.getIntrinsicWidth();
            h = scendHand.getIntrinsicHeight();
            // System.out.println("second   "+(x-(w/2))+" "+(y -h)+" "+(x + (w / 2))+" "+(y));
            // scendHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
            scendHand.setBounds(0, 0, availableWidth, availableHeight);
            // scendHand.setBounds(x - (w / 2), y - h, x + (w / 2), y);
        }
        scendHand.draw(canvas);
        canvas.restore();

        if (scaled) {
            canvas.restore();
        }
    }

    private void onTimeChanged() {

        mCalendar.setToNow();

        int hour = mCalendar.hour;
        int minute = mCalendar.minute;
        int second = mCalendar.second;

        if (hour <= 6 || hour >= 18) {
            isnight = true;
            if (isnight) {
                is = r.openRawResource(R.drawable.ic_clock_black);
                mDialDrawable = new BitmapDrawable(is);

                is = r.openRawResource(R.drawable.ic_hour_white);
                mHourHandDrawable = new BitmapDrawable(is);

                is = r.openRawResource(R.drawable.ic_minute_white);
                mMinuteHandDrawable = new BitmapDrawable(is);

                isnight = false;
            }

        } else {
            isnight = true;
            if (isnight) {
                is = r.openRawResource(R.drawable.ic_clock_white);
                mDialDrawable = new BitmapDrawable(is);

                is = r.openRawResource(R.drawable.ic_hour_black);
                mHourHandDrawable = new BitmapDrawable(is);

                is = r.openRawResource(R.drawable.ic_minute_black);
                mMinuteHandDrawable = new BitmapDrawable(is);

                isnight = false;
            }
        }

        mSeconds = second;
        mMinutes = minute + second / 60.0f;
        mHours = hour + mMinutes / 60.0f;

        mChanged = true;
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String tz = "";
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                tz = intent.getStringExtra("time-zone");
                mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
                time_zone = mCalendar.timezone;
            }
            onTimeChanged();
            invalidate();
        }
    };
}
