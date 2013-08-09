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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RepeatWeek extends Activity {

    private LinearLayout firstLayout;
    private LinearLayout secondLayout;
    private LinearLayout thirdLayout;
    private LinearLayout fourLayout;
    private LinearLayout fiveLayout;
    private LinearLayout sixLayout;
    private LinearLayout sevenLayout;

    private ImageView firstArrow;
    private ImageView secondArrow;
    private ImageView thirdArrow;
    private ImageView fourArrow;
    private ImageView fiveArrow;
    private ImageView sixArrow;
    private ImageView sevenArrow;

    private Button returnBtn;

    private boolean fistSelect;
    private boolean secondSelect;
    private boolean thirdSelect;
    private boolean fourSelect;
    private boolean fiveSelect;
    private boolean sixSelect;
    private boolean senvenSelect;

    private Alarm.DaysOfWeek mNewDaysOfWeek = new Alarm.DaysOfWeek(0);
    private Alarm.DaysOfWeek mDaysOfWeek = new Alarm.DaysOfWeek(0);

    boolean[] array;
    private TextView firstView;
    private TextView secondView;
    private TextView thirdView;
    private TextView fourView;
    private TextView fiveView;
    private TextView sixView;
    private TextView senvenView;
    private Typeface mTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.repeatweek);

        Intent intent = getIntent();
        Alarm.DaysOfWeek week = (Alarm.DaysOfWeek) intent.getSerializableExtra("dayweek");

        mNewDaysOfWeek.set(week);
        mDaysOfWeek.set(week);

        array = mNewDaysOfWeek.getBooleanArray();
        mTypeface = BusinessUtils.getBoldTypeface(getApplicationContext());

        init();

        returnBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mDaysOfWeek.set(mNewDaysOfWeek);
                Intent intent = new Intent(RepeatWeek.this, SetAlarmClock.class);
                intent.putExtra("dayWeek", mDaysOfWeek);
                intent.putExtra("week", mDaysOfWeek.toString(RepeatWeek.this, true));
                setResult(1, intent);
                finish();
            }
        });

        firstLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (fistSelect) {
                    mNewDaysOfWeek.set(0, false);
                    fistSelect = false;
                    firstArrow.setVisibility(View.INVISIBLE);
                } else {
                    mNewDaysOfWeek.set(0, true);
                    fistSelect = true;
                    firstArrow.setVisibility(View.VISIBLE);
                }
            }
        });

        secondLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (secondSelect) {
                    mNewDaysOfWeek.set(1, false);
                    secondSelect = false;
                    secondArrow.setVisibility(View.INVISIBLE);
                } else {
                    mNewDaysOfWeek.set(1, true);
                    secondSelect = true;
                    secondArrow.setVisibility(View.VISIBLE);
                }
            }
        });
        thirdLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (thirdSelect) {
                    mNewDaysOfWeek.set(2, false);
                    thirdSelect = false;
                    thirdArrow.setVisibility(View.INVISIBLE);
                } else {
                    mNewDaysOfWeek.set(2, true);
                    thirdSelect = true;
                    thirdArrow.setVisibility(View.VISIBLE);
                }
            }
        });
        fourLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (fourSelect) {
                    mNewDaysOfWeek.set(3, false);
                    fourSelect = false;
                    fourArrow.setVisibility(View.INVISIBLE);
                } else {
                    mNewDaysOfWeek.set(3, true);
                    fourSelect = true;
                    fourArrow.setVisibility(View.VISIBLE);
                }
            }
        });
        fiveLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (fiveSelect) {
                    mNewDaysOfWeek.set(4, false);
                    fiveSelect = false;
                    fiveArrow.setVisibility(View.INVISIBLE);
                } else {
                    mNewDaysOfWeek.set(4, true);
                    fiveSelect = true;
                    fiveArrow.setVisibility(View.VISIBLE);
                }
            }
        });
        sixLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sixSelect) {
                    mNewDaysOfWeek.set(5, false);
                    sixSelect = false;
                    sixArrow.setVisibility(View.INVISIBLE);
                } else {
                    mNewDaysOfWeek.set(5, true);
                    sixSelect = true;
                    sixArrow.setVisibility(View.VISIBLE);
                }
            }
        });
        sevenLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (senvenSelect) {
                    mNewDaysOfWeek.set(6, false);
                    senvenSelect = false;
                    sevenArrow.setVisibility(View.INVISIBLE);
                } else {
                    mNewDaysOfWeek.set(6, true);
                    senvenSelect = true;
                    sevenArrow.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mDaysOfWeek.set(mNewDaysOfWeek);
            Intent intent = new Intent(RepeatWeek.this, SetAlarmClock.class);
            intent.putExtra("dayWeek", mDaysOfWeek);
            intent.putExtra("week", mDaysOfWeek.toString(this, true));
            setResult(1, intent);
            finish();
        }

        return true;
    }

    public void init() {

        firstLayout = (LinearLayout) findViewById(R.id.first_layout);
        secondLayout = (LinearLayout) findViewById(R.id.second_layout);
        thirdLayout = (LinearLayout) findViewById(R.id.third_layout);
        fourLayout = (LinearLayout) findViewById(R.id.four_layout);
        fiveLayout = (LinearLayout) findViewById(R.id.five_layout);
        sixLayout = (LinearLayout) findViewById(R.id.six_layout);
        sevenLayout = (LinearLayout) findViewById(R.id.seven_layout);

        firstView = (TextView) findViewById(R.id.first_text);
        secondView = (TextView) findViewById(R.id.second_text);
        thirdView = (TextView) findViewById(R.id.third_text);
        fourView = (TextView) findViewById(R.id.four_text);
        fiveView = (TextView) findViewById(R.id.five_text);
        sixView = (TextView) findViewById(R.id.six_text);
        senvenView = (TextView) findViewById(R.id.seven_text);

        firstView.setTypeface(mTypeface);
        secondView.setTypeface(mTypeface);
        thirdView.setTypeface(mTypeface);
        fourView.setTypeface(mTypeface);
        fiveView.setTypeface(mTypeface);
        sixView.setTypeface(mTypeface);
        senvenView.setTypeface(mTypeface);

        firstArrow = (ImageView) findViewById(R.id.first_arrow);
        secondArrow = (ImageView) findViewById(R.id.second_arrow);
        thirdArrow = (ImageView) findViewById(R.id.third_arrow);
        fourArrow = (ImageView) findViewById(R.id.four_arrow);
        fiveArrow = (ImageView) findViewById(R.id.five_arrow);
        sixArrow = (ImageView) findViewById(R.id.six_arrow);
        sevenArrow = (ImageView) findViewById(R.id.seven_arrow);
        returnBtn = (Button) findViewById(R.id.return_btn);

        for (int i = 0; i < array.length; i++) {
            // System.out.println(array[i]);
            if (i == 0) {
                fistSelect = array[i];
                if (fistSelect) {
                    firstArrow.setVisibility(View.VISIBLE);
                }
            } else if (i == 1) {
                secondSelect = array[i];
                if (secondSelect) {
                    secondArrow.setVisibility(View.VISIBLE);
                }
            } else if (i == 2) {
                thirdSelect = array[i];
                if (thirdSelect) {
                    thirdArrow.setVisibility(View.VISIBLE);
                }
            } else if (i == 3) {
                fourSelect = array[i];
                if (fourSelect) {
                    fourArrow.setVisibility(View.VISIBLE);
                }
            } else if (i == 4) {
                fiveSelect = array[i];
                if (fiveSelect) {
                    fiveArrow.setVisibility(View.VISIBLE);
                }
            } else if (i == 5) {
                sixSelect = array[i];
                if (sixSelect) {
                    sixArrow.setVisibility(View.VISIBLE);
                }
            } else if (i == 6) {
                senvenSelect = array[i];
                if (senvenSelect) {
                    sevenArrow.setVisibility(View.VISIBLE);
                }
            }
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
