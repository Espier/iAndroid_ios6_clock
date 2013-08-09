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

import org.espier.clock.bean.TimeBean;
import org.espier.clock.util.BusinessUtils;
import org.espier.clock.util.Constant;
import org.espier.clock.util.ShareFileUtils;
import org.espier.clock.wheel.NumericWheelAdapter;
import org.espier.clock.wheel.OnWheelChangedListener;
import org.espier.clock.wheel.WheelView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Timer extends Activity {

    public static final String TAG = Timer.class.getSimpleName();
    // Scrolling flag
    private WheelView mHourView;
    private WheelView mMinutesView;

    private Button startBtn;
    private Button cancelBtn;
    private Button stopBtn;
    private TextView timeWatch;
    private TextView audioName;
    private TextView textEnd;
    private ImageView arrayImage;

    private LinearLayout chooseTimeLayout;
    private RelativeLayout timingLayout;
    private RelativeLayout buttomLayout;

    private TimeBean times = new TimeBean();
    private int minuteValue = 1;
    private int hourValue;
    private int secondValue;
    private boolean isRunning = true;

    private Uri alert;
    private String mDefaultName;

    private Handler handler = new Handler();
    private Typeface mTypeface;

    private final String MUSIC_LABLE = "alter";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);

        ShareFileUtils.setContext(this);

        mTypeface = BusinessUtils.getTypeface(getApplicationContext());
        System.out.println("Timer  onCreate");
        startBtn = (Button) findViewById(R.id.startBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);


        timeWatch = (TextView) findViewById(R.id.timewatch);

        chooseTimeLayout = (LinearLayout) findViewById(R.id.choosetimelayout);
        timingLayout = (RelativeLayout) findViewById(R.id.timinglayout);
        buttomLayout = (RelativeLayout) findViewById(R.id.buttomLayout);
        audioName = (TextView) findViewById(R.id.audioName);
        textEnd = (TextView) findViewById(R.id.textEnd);
        arrayImage = (ImageView) findViewById(R.id.arrayImage);
        textEnd.setText(getString(R.string.time_over));

        // textEnd.setTypeface(mTypeface);
        // audioName.setTypeface(mTypeface);
        timeWatch.setTypeface(mTypeface);
        startBtn.setTypeface(mTypeface);
        cancelBtn.setTypeface(mTypeface);
        stopBtn.setTypeface(mTypeface);

        mHourView = (WheelView) findViewById(R.id.hour);
        mHourView.setLabel(getString(R.string.hour));
        mHourView.setVisibleItems(5);
        mHourView.setAdapter(new NumericWheelAdapter(0, 23));
        mHourView.TEXT_SIZE = getResources().getDimensionPixelSize(R.dimen.data_text_size_1);

        mMinutesView = (WheelView) findViewById(R.id.minutes);
        mMinutesView.setCyclic(true);
        mMinutesView.setLabel(getString(R.string.minutes));
        mMinutesView.setVisibleItems(5);
        mMinutesView.setAdapter(new NumericWheelAdapter(0, 59));
        mMinutesView.TEXT_SIZE = getResources().getDimensionPixelSize(R.dimen.data_text_size_1);

        String saveAlert = ShareFileUtils.getString(MUSIC_LABLE, "");
        if ("".equals(saveAlert)) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Constant.uri = alert;
            mDefaultName = alert.toString();
            mDefaultName = mDefaultName.substring(mDefaultName.lastIndexOf("/") + 1);
            audioName.setText(getString(R.string.default_alarm));
        } else {
            alert = Uri.parse(saveAlert);
            mDefaultName = BusinessUtils.queryAudioName(this, alert);
            audioName.setText(mDefaultName);
        }

        System.out.println(" TEXT_SIZE " + mMinutesView.TEXT_SIZE + "  alert " + alert.toString());


        mMinutesView.setCurrentItem(1);

        mHourView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                hourValue = newValue;
            }
        });


        mMinutesView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                minuteValue = newValue;
            }
        });

        startBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hourValue == 0 && minuteValue == 0) {
                    Toast.makeText(Timer.this, "小时和分钟不能同时设为0", Toast.LENGTH_SHORT).show();
                    return;
                }
                chooseTimeLayout.setVisibility(View.INVISIBLE);
                startBtn.setVisibility(View.INVISIBLE);

                timeWatch.setVisibility(View.VISIBLE);
                buttomLayout.setVisibility(View.VISIBLE);

                times.hour = hourValue;
                times.minute = minuteValue;
                times.second = secondValue;
                handler.postDelayed(runTime, 100);

            }
        });

        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                chooseTimeLayout.setVisibility(View.VISIBLE);
                startBtn.setVisibility(View.VISIBLE);

                timeWatch.setVisibility(View.INVISIBLE);
                buttomLayout.setVisibility(View.INVISIBLE);

                handler.removeCallbacks(runTime);
                times.hour = 0;
                times.minute = 0;
                times.second = 0;

                isRunning = true;


            }
        });

        stopBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isRunning) {
                    stop();
                } else {
                    start();
                }

            }
        });


        timingLayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    timingLayout.setBackgroundResource(R.drawable.ic_button_big_blue);
                    audioName.setTextColor(Color.WHITE);
                    textEnd.setTextColor(Color.WHITE);
                    arrayImage.setBackgroundResource(R.drawable.ic_arrows_white);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    timingLayout.setBackgroundResource(R.drawable.ic_button_big_blue);
                    audioName.setTextColor(Color.WHITE);
                    textEnd.setTextColor(Color.WHITE);
                    arrayImage.setBackgroundResource(R.drawable.ic_arrows_white);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    timingLayout.setBackgroundResource(R.drawable.ic_button_big_gray);
                    audioName.setTextColor(Color.WHITE);
                    textEnd.setTextColor(Color.BLACK);
                    arrayImage.setBackgroundResource(R.drawable.ic_arrows_black);

                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, alert);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, false);
                    startActivityForResult(intent, Constant.RINGTONE_PICKED);
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    timingLayout.setBackgroundResource(R.drawable.ic_button_big_gray);
                    audioName.setTextColor(Color.WHITE);
                    textEnd.setTextColor(Color.BLACK);
                    arrayImage.setBackgroundResource(R.drawable.ic_arrows_black);
                }
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case Constant.RINGTONE_PICKED: {
                alert = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                System.out.println(" Timer   alert " + alert);
                if (null == alert) {
                    alert = Constant.uri;
                } else {
                    Constant.uri = alert;
                }
                String musicName = BusinessUtils.queryAudioName(this, alert);
                if (!musicName.equals(mDefaultName)) {
                    audioName.setText(musicName);
                } else {
                    audioName.setText(getString(R.string.default_alarm));
                }
                ShareFileUtils.setString(MUSIC_LABLE, alert.toString());
                break;
            }
        }
    }



    public void stop() {
        stopBtn.setText(R.string.continues);
        handler.removeCallbacks(runTime);
        isRunning = false;
    }

    public void start() {
        stopBtn.setText(R.string.stop);
        handler.postDelayed(runTime, 1000);
        isRunning = true;
    }


    public boolean isEndcount(TimeBean timeBean) {
        if (timeBean.hour == 0 && timeBean.minute == 0 && timeBean.second == 0) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 刷新线程
     */
    private Runnable runTime = new Runnable() {

        @Override
        public void run() {
            if (!isEndcount(times)) {
                countDown();
            }

            if (times.hour == 0) {
                if (times.second < 10 && times.minute < 10) {
                    timeWatch.setText("0" + times.minute + ":" + "0" + times.second);
                } else if (times.minute < 10) {
                    timeWatch.setText("0" + times.minute + ":" + times.second);
                } else if (times.second < 10) {
                    timeWatch.setText(times.minute + ":" + "0" + times.second);
                } else {
                    timeWatch.setText(times.minute + ":" + times.second);
                }
            } else {
                if (times.hour < 10 && times.minute < 10 && times.second < 10) {
                    timeWatch.setText("0" + times.hour + ":" + "0" + times.minute + ":" + "0"
                            + times.second);
                } else if (times.hour < 10 && times.minute < 10) {
                    timeWatch.setText("0" + times.hour + ":" + "0" + times.minute + ":"
                            + times.second);
                } else if (times.hour < 10 && times.second < 10) {
                    timeWatch.setText("0" + times.hour + ":" + times.minute + ":" + "0"
                            + times.second);
                } else if (times.minute < 10 && times.second < 10) {
                    timeWatch.setText(times.hour + ":" + "0" + times.minute + ":" + "0"
                            + times.second);
                } else if (times.hour < 10) {
                    timeWatch.setText("0" + times.hour + ":" + times.minute + ":" + times.second);
                } else if (times.minute < 10) {
                    timeWatch.setText(times.hour + ":" + "0" + times.minute + ":" + times.second);
                } else if (times.second < 10) {
                    timeWatch.setText(times.hour + ":" + times.minute + ":" + "0" + times.second);
                } else {
                    timeWatch.setText(times.hour + ":" + times.minute + ":" + times.second);
                }

            }
            if (isEndcount(times)) {
                timeWatch.setVisibility(View.INVISIBLE);
                buttomLayout.setVisibility(View.INVISIBLE);

                chooseTimeLayout.setVisibility(View.VISIBLE);
                startBtn.setVisibility(View.VISIBLE);

                handler.removeCallbacks(runTime);
                Intent intent = new Intent(Alarms.TIME_ALARM_ACTION);
                sendBroadcast(intent);

            } else {
                handler.postDelayed(runTime, 1000);
            }

        }
    };



    /**
     * count down
     */
    private void countDown() {
        if (times.second != 0) {
            times.second -= 1;
            return;
        }
        if (times.second == 0 && times.minute != 0) { // 秒为0
            times.second = 59;
            times.minute -= 1;
            return;

        } else if (times.second == 0 && times.minute == 0 && times.hour != 0) {
            times.second = 59;
            times.minute = 59;
            times.hour -= 1;
            return;

        } else if (times.second == 0 && times.minute == 0 && times.hour == 0) {
            times.second = 59;
            times.minute = 59;
            times.hour = 23;
            return;
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runTime);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

}
