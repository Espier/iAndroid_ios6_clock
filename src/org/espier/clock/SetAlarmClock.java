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

package org.espier.clock;

import java.util.Calendar;

import org.espier.clock.util.BusinessUtils;
import org.espier.clock.util.Constant;
import org.espier.clock.util.ShareFileUtils;
import org.espier.clock.wheel.ArrayWheelAdapter;
import org.espier.clock.wheel.NumericWheelAdapter;
import org.espier.clock.wheel.OnWheelChangedListener;
import org.espier.clock.wheel.WheelView;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetAlarmClock extends Activity implements OnClickListener {

    public static final boolean DEBUG = false;
    private Button cancelBtn;
    private Button saveBtn;

    private WheelView hourView;
    private WheelView minuteView;
    private WheelView mAmPmView;

    private LinearLayout repeatLayout;
    private LinearLayout soundLayout;
    private LinearLayout alarmNameLayout;

    private TextView textRepeat;
    private TextView textSound;
    private TextView textName;
    private ImageView imgCheckon;

    private TextView repeatLable;
    private TextView soundLable;
    private TextView caulkLable;
    private TextView nameLable;


    private int hourValue;
    private int minuteValue;
    private int times;

    private int mId;
    private Alarm mOriginalAlarm;


    private boolean isCheckOff = true;
    private String label;
    private Uri delfaultAlarm;
    private String delfautlAlarmName;
    // Initial value that can be set with the values saved in the database.
    private Alarm.DaysOfWeek mDaysOfWeek = new Alarm.DaysOfWeek(0);

    private Calendar mCalendar;
    private String[] items;
    private int mAmPm = 0;

    private Typeface mTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.browser_setting_main);

        ShareFileUtils.setContext(this);

        items =
                new String[] {getResources().getString(R.string.am_text),
                        getResources().getString(R.string.pm_text)};

        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        saveBtn = (Button) findViewById(R.id.save_btn);
        repeatLayout = (LinearLayout) findViewById(R.id.repeat_layout);
        soundLayout = (LinearLayout) findViewById(R.id.sound_layout);
        alarmNameLayout = (LinearLayout) findViewById(R.id.alarm_name_layout);

        mTypeface = BusinessUtils.getTypeface(getApplicationContext());

        textRepeat = (TextView) findViewById(R.id.text_repeat);
        textSound = (TextView) findViewById(R.id.text_sound);
        textName = (TextView) findViewById(R.id.text_name);
        imgCheckon = (ImageView) findViewById(R.id.imge_checkon);

        repeatLable = (TextView) findViewById(R.id.repeat_lable);
        soundLable = (TextView) findViewById(R.id.sound_lable);
        caulkLable = (TextView) findViewById(R.id.caulk_lable);
        nameLable = (TextView) findViewById(R.id.name_lable);

        textRepeat.setTypeface(mTypeface);
        textSound.setTypeface(mTypeface);
        textName.setTypeface(mTypeface);
        repeatLable.setTypeface(mTypeface);
        soundLable.setTypeface(mTypeface);
        caulkLable.setTypeface(mTypeface);
        nameLable.setTypeface(mTypeface);


        repeatLayout.setOnClickListener(this);
        soundLayout.setOnClickListener(this);
        alarmNameLayout.setOnClickListener(this);
        imgCheckon.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        Intent i = getIntent();
        mId = i.getIntExtra(Alarms.ALARM_ID, -1);
        Alarm alarm = null;
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        if (mId == -1) {
            alarm = new Alarm(this);
            hourValue = alarm.hour;
            minuteValue = alarm.minutes;
        } else {
            alarm = Alarms.getAlarm(getContentResolver(), mId);
            if (alarm == null) {
                finish();
                return;
            }
            hourValue = alarm.hour;
            minuteValue = alarm.minutes;
            System.out.println(" hourValue  " + hourValue);
        }

        mOriginalAlarm = alarm;
        updateView(mOriginalAlarm);

        mAmPmView = (WheelView) findViewById(R.id.am_pm);
        mAmPmView.TEXT_SIZE = getResources().getDimensionPixelSize(R.dimen.am_pm_text_size);


        hourView = (WheelView) findViewById(R.id.hour);
        hourView.setVisibleItems(5);
        hourView.setCyclic(true);
        hourView.TEXT_SIZE = getResources().getDimensionPixelSize(R.dimen.data_text_size_2);
        if (BusinessUtils.getSystemTime_12_24(this)) {
            hourView.setAdapter(new NumericWheelAdapter(0, 23));

            if (mId == -1) {
                Time time = new Time();
                time.setToNow();
                hourValue = time.hour;
                minuteValue = time.minute;
            }
            hourView.setCurrentItem(hourValue);
        } else {
            System.out.println(" ....hourValue...... " + hourValue + "  minuteValue  "
                    + minuteValue);
            if (hourValue > 12) {
                hourValue = hourValue - 12;
            }
            System.out.println(" ....hourValue...... " + hourValue + "  minuteValue  "
                    + minuteValue);
            hourView.setAdapter(new NumericWheelAdapter(1, 12));
            if (hourValue == 0) {
                hourView.setCurrentItem(12);
            } else {
                hourView.setCurrentItem(hourValue - 1);
            }

            // visible am pm View
            mAmPmView.setVisibility(View.VISIBLE);
            mAmPmView.setAdapter(new ArrayWheelAdapter<String>(items));
            if (mId == -1) {
                if (mCalendar.get(Calendar.AM_PM) == 0) {
                    mAmPmView.setCurrentItem(0);
                    mAmPm = 0;
                } else {
                    mAmPmView.setCurrentItem(1);
                    mAmPm = 1;
                }
            } else {
                System.out.println(" ....hourValue...... " + alarm.interval);
                if (alarm.interval == 0) {
                    mAmPmView.setCurrentItem(0);
                    mAmPm = 0;
                } else {
                    mAmPmView.setCurrentItem(1);
                    mAmPm = 1;
                }
            }
        }

        minuteView = (WheelView) findViewById(R.id.minutes);
        minuteView.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        minuteView.setVisibleItems(5);
        minuteView.setCurrentItem(minuteValue);
        minuteView.setCyclic(true);
        minuteView.TEXT_SIZE = getResources().getDimensionPixelSize(R.dimen.data_text_size_2);

        hourView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                System.out.println(" newValue " + newValue);

                if (BusinessUtils.getSystemTime_12_24(SetAlarmClock.this)) {
                    hourValue = newValue;
                } else {
                    newValue += 1;
                    if (newValue == 12) {
                        hourValue = 0;
                    } else {
                        hourValue = newValue;
                    }
                }

            }
        });

        minuteView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                minuteValue = newValue;
            }
        });

        mAmPmView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                System.out.println(" oldValue " + oldValue + "  newValue " + newValue);
                mAmPm = newValue;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                Intent cancel = new Intent(this, AlarmClock.class);
                setResult(1, cancel);
                finish();
                break;
            case R.id.save_btn:
                saveAlarm();
                Intent intent = new Intent(this, AlarmClock.class);
                setResult(1, intent);
                finish();
                break;
            case R.id.repeat_layout:
                Intent repeatIntent = new Intent(this, RepeatWeek.class);
                repeatIntent.putExtra("dayweek", mDaysOfWeek);
                repeatIntent.putExtra("repeat", textRepeat.getText().toString());
                startActivityForResult(repeatIntent, 0);
                break;
            case R.id.sound_layout:
                Intent soundIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                soundIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                soundIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, false);
                if (delfaultAlarm != null) {
                    soundIntent
                            .putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, delfaultAlarm);
                }
                startActivityForResult(soundIntent, Constant.RINGTONE_PICKED);
                break;
            case R.id.alarm_name_layout:
                displayDialog();
                break;
            case R.id.imge_checkon:
                checkOffer();
                break;
        }
    }

    private long saveAlarm() {
        Alarm alarm = new Alarm(this);
        alarm.id = mId;
        alarm.enabled = true;
        alarm.daysOfWeek = mDaysOfWeek;
        alarm.vibrate = true;
        alarm.label = label;
        alarm.alert = delfaultAlarm;
        alarm.soundName = delfautlAlarmName;
        alarm.times = times;
        alarm.minutes = minuteValue;


        if (BusinessUtils.getSystemTime_12_24(this)) {
            alarm.hour = hourValue;
            alarm.interval = 0;
        } else {
            if (mAmPm == 0) {
                alarm.hour = hourValue;
                alarm.interval = 0;
            } else {
                alarm.hour = hourValue + 12;
                alarm.interval = 1;
            }
        }

        long time;
        if (alarm.id == -1) {
            time = Alarms.addAlarm(this, alarm);
            mId = alarm.id;
        } else {
            time = Alarms.setAlarm(this, alarm);
        }
        return time;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            Bundle bundle = data.getExtras();
            mDaysOfWeek = (Alarm.DaysOfWeek) bundle.getSerializable("dayWeek");
            textRepeat.setText(mDaysOfWeek.toString(this, true));
            textRepeat.setText(bundle.getString("week"));
        } else if (requestCode == Constant.RINGTONE_PICKED) {
            if (resultCode != RESULT_OK) {
                return;
            }

            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (null != uri) {
                delfaultAlarm = uri;
            }
            delfautlAlarmName = BusinessUtils.queryAudioName(this, delfaultAlarm);
            textSound.setText(delfautlAlarmName);
        }

    }

    public void checkOffer() {
        if (isCheckOff) {
            imgCheckon.setBackgroundResource(R.drawable.ic_checkoff);
            isCheckOff = false;
            times = 0;
            Constant.CAULK_OFF = 0;
        } else {
            imgCheckon.setBackgroundResource(R.drawable.ic_checkon);
            isCheckOff = true;
            times = 10;
            Constant.CAULK_OFF = 10;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent cancel = new Intent(this, AlarmClock.class);
            setResult(1, cancel);
            finish();
        }

        return true;
    }

    public void displayDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view, null);
        final EditText text = (EditText) view.findViewById(R.id.edit_title_name);
        text.setText(mOriginalAlarm.label);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                label = text.getText().toString();
                textName.setText(label);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();
    }


    private void updateView(Alarm alarm) {
        mId = alarm.id;
        mDaysOfWeek = alarm.daysOfWeek;
        textRepeat.setText(alarm.daysOfWeek.toString(this, true));
        textName.setText(alarm.label);
        label = alarm.label;
        delfaultAlarm = alarm.alert;
        delfautlAlarmName = alarm.soundName;

        textSound.setText(delfautlAlarmName);
        if (alarm.times == 10) {
            isCheckOff = true;
            imgCheckon.setBackgroundResource(R.drawable.ic_checkon);
            times = 10;
        } else {
            isCheckOff = false;
            imgCheckon.setBackgroundResource(R.drawable.ic_checkoff);
            times = 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
