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

import org.espier.clock.util.BusinessUtils;
import org.espier.clock.widget.DigitalClock;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class Stopwatch extends Activity {

    public static final String TAG = Stopwatch.class.getSimpleName();

    private DigitalClock mLapwatch;
    private DigitalClock mMainwatch;
    private Button mLeftButton;
    private Button mRightButton;
    private ListView mLapsView;
    private LapsAdapter mLapsAdapter;

    private boolean isRunning;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stopwatch);

        mLapwatch = (DigitalClock) findViewById(R.id.lapwatch);
        mMainwatch = (DigitalClock) findViewById(R.id.mainwatch);
        mLeftButton = (Button) findViewById(R.id.leftbutton);
        mRightButton = (Button) findViewById(R.id.rightbutton);

        mLeftButton.setTypeface(BusinessUtils.getTypeface(getApplicationContext()));
        mRightButton.setTypeface(BusinessUtils.getTypeface(getApplicationContext()));
        mLapsView = (ListView) findViewById(R.id.laps);

        mLapsAdapter = new LapsAdapter(this);
        mLapsView.setAdapter(mLapsAdapter);

        mLeftButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (isRunning) {
                    stop();
                } else {
                    start();
                }
            }
        });

        mRightButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (isRunning) {
                    lap();
                } else {
                    reset();
                }
            }
        });

    }

    private void start() {
        Drawable background = getResources().getDrawable(R.drawable.ic_button_red);
        mMainwatch.start();
        mLapwatch.start();

        isRunning = true;
        mLeftButton.setText(R.string.stop);
        mLeftButton.setBackgroundDrawable(background);
        mRightButton.setEnabled(true);
        mRightButton.setText(R.string.lap);
        mRightButton.setTextColor(Color.WHITE);
    }

    private void stop() {
        Drawable background = getResources().getDrawable(R.drawable.ic_button_green);
        mMainwatch.stop();
        mLapwatch.stop();
        isRunning = false;
        mLeftButton.setText(R.string.start);
        mLeftButton.setBackgroundDrawable(background);
        mRightButton.setText(R.string.reset);
    }

    private void reset() {
        mMainwatch.reset();
        mLapwatch.reset();
        mLapsAdapter.clear();
        mRightButton.setEnabled(false);
        mRightButton.setTextColor(Color.LTGRAY);
    }

    private void lap() {
        mLapwatch.stop();
        mLapsAdapter.addLap(mLapwatch.getText().toString());
        mLapwatch.reset();
        mLapwatch.start();
    }

    public void addItem(){

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
