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

import java.util.ArrayList;
import java.util.HashMap;

import org.espier.clock.Alarm.Columns;
import org.espier.clock.util.BusinessUtils;
import org.espier.clock.util.ShareFileUtils;
import org.espier.clock.widget.AnalogClock;
import org.espier.clock.widget.TimeView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class WorldClock extends Activity {

    public static final String TAG = WorldClock.class.getSimpleName();

    private Button editBtn;
    private Button addBtn;

    private ArrayList<Alarm> worldAlarmList = new ArrayList<Alarm>();

    private LinearLayout contentlayout;

    private RelativeLayout relativeLayout;


    private boolean isState = true;
    private boolean isDelete = true;
    private int position;
    private int tempPosition;
    private int id;
    private int tempId;

    private Cursor mCursor;

    // android.widget.AnalogClock

    // addVeiw init
    private TextView clockCity;
    private ImageView clockEdit;
    private ImageView imgeArrow;
    private Button deleteBtn;
    private LinearLayout clockTimeLayout;
    private TextView mNoWorldClock;
    private boolean isInited = false;
    private ImageView mTopBg;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worldclock);

        ShareFileUtils.setContext(this);
        editBtn = (Button) findViewById(R.id.edit_btn);
        addBtn = (Button) findViewById(R.id.add_btn);
        contentlayout = (LinearLayout) findViewById(R.id.contentLayout);
        mNoWorldClock = (TextView) findViewById(R.id.no_world_clock);
        mTopBg = (ImageView) findViewById(R.id.top_bg);
        isInited = ShareFileUtils.getBoolean("init_native_clock", false);

        if (!isInited) {
            initNativeClock();
        }

        mCursor = Alarms.getWorldAlarmsCursor(getContentResolver());
        worldAlarmList = getAlarmList(mCursor);

        initView();
        editBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isState) {
                    editBtn.setBackgroundResource(R.drawable.ic_button_2);
                    editBtn.setText(getResources().getString(R.string.finish));
                    editClock(isState);
                    isState = false;
                } else {
                    editBtn.setBackgroundResource(R.drawable.ic_button_1);
                    editBtn.setText(getResources().getString(R.string.edit));
                    editClock(isState);
                    isState = true;
                    isDelete = true;
                }

            }
        });

        addBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isState) {
                    Intent intent = new Intent(WorldClock.this, SearchWroldClock.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

    }


    public void initView() {
        int size = worldAlarmList.size();
        if (size == 0) {
            editBtn.setEnabled(false);
        } else {
            editBtn.setEnabled(true);
        }
        setNoWorldClockTextView();
        contentlayout.removeAllViews();
        Alarm alarm;
        if (size >= 3 || size < 1) {
            mTopBg.setVisibility(View.GONE);
        } else {
            mTopBg.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < size; i++) {
            alarm = worldAlarmList.get(i);
            relativeLayout =
                    (RelativeLayout) LayoutInflater.from(this).inflate(
                            R.layout.worldclock_list_item, null);
            // clock
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            AnalogClock analogClock = (AnalogClock) relativeLayout.findViewById(R.id.analogClock);
            analogClock.init(this, alarm.timezone);

            // time
            clockTimeLayout =
                    (LinearLayout) relativeLayout.findViewById(R.id.clock_item_time_layout);
            LinearLayout.LayoutParams linearParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            TimeView clockTime = new TimeView(this);
            clockTime.init(alarm.timezone);
            clockTime.setTextColor(Color.BLACK);
            clockTime.setShadowLayer(1, 0, 2, Color.WHITE);
            clockTime.setSingleLine();
            clockTime.setTextSize(getResources()
                    .getDimensionPixelSize(R.dimen.world_time_text_size));
            clockTime.setTypeface(BusinessUtils.getBoldTypeface(getApplicationContext()));
            clockTimeLayout.addView(clockTime, linearParams);

            // day
            LinearLayout.LayoutParams dayParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView textDay = new TextView(this);
            textDay.setTextColor(getResources().getColor(R.color.world_item_tody));
            textDay.setShadowLayer(1, 0, 2, Color.WHITE);
            textDay.setSingleLine();
            textDay.setTypeface(BusinessUtils.getTypeface(getApplicationContext()));
            textDay.setText(BusinessUtils.getDay(this, alarm.timezone));
            clockTimeLayout.addView(textDay, dayParams);

            clockCity = (TextView) relativeLayout.findViewById(R.id.clock_item_city);
            clockCity.setTypeface(BusinessUtils.getTypeface(getApplicationContext()));
            clockCity.setText(alarm.country);
            contentlayout.addView(relativeLayout);
        }
    }

    public void editClock(boolean isState) {

        int childrenCount = contentlayout.getChildCount();
        RelativeLayout layout;
        for (int i = 0; i < childrenCount; i++) {
            layout = (RelativeLayout) contentlayout.getChildAt(i);
            clockEdit = (ImageView) layout.findViewById(R.id.clock_item_img_edit);
            imgeArrow = (ImageView) layout.findViewById(R.id.alarm_item_img_arrow);
            clockTimeLayout = (LinearLayout) layout.findViewById(R.id.clock_item_time_layout);
            deleteBtn = (Button) layout.findViewById(R.id.alarm_item_img_delete);
            clockEdit.setOnClickListener(editClick);


            if (isState) {
                clockEdit.setVisibility(View.VISIBLE);
                imgeArrow.setVisibility(View.VISIBLE);
                clockTimeLayout.setVisibility(View.GONE);
            } else {
                clockEdit.setBackgroundResource(R.drawable.ic_edit_landscape);
                clockEdit.setVisibility(View.GONE);
                imgeArrow.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
                clockTimeLayout.setVisibility(View.VISIBLE);

            }
            clockEdit.setTag(i);
        }


    }


    OnClickListener editClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            System.out.println(" onClick  isDelete " + isDelete);

            position = (Integer) v.getTag();
            RelativeLayout layout;
            id = worldAlarmList.get(position).id;
            if (isDelete) {
                isDelete = false;
                tempId = id;
                tempPosition = position;

                layout = (RelativeLayout) contentlayout.getChildAt(position);
                clockEdit = (ImageView) layout.findViewById(R.id.clock_item_img_edit);
                deleteBtn = (Button) layout.findViewById(R.id.alarm_item_img_delete);
                imgeArrow = (ImageView) layout.findViewById(R.id.alarm_item_img_arrow);
                imgeArrow.setVisibility(View.GONE);
                deleteBtn.setOnClickListener(deleteClick);
                deleteBtn.setVisibility(View.VISIBLE);
                clockEdit.setBackgroundResource(R.drawable.ic_edit_portrait);

            } else {
                if (id != tempId && !isDelete) {

                    layout = (RelativeLayout) contentlayout.getChildAt(position);
                    clockEdit = (ImageView) layout.findViewById(R.id.clock_item_img_edit);
                    clockEdit.setBackgroundResource(R.drawable.ic_edit_landscape);

                    layout = (RelativeLayout) contentlayout.getChildAt(tempPosition);
                    clockEdit = (ImageView) layout.findViewById(R.id.clock_item_img_edit);
                    clockEdit.setBackgroundResource(R.drawable.ic_edit_landscape);
                    deleteBtn = (Button) layout.findViewById(R.id.alarm_item_img_delete);
                    deleteBtn.setVisibility(View.GONE);
                    imgeArrow = (ImageView) layout.findViewById(R.id.alarm_item_img_arrow);
                    imgeArrow.setVisibility(View.VISIBLE);


                } else {
                    layout = (RelativeLayout) contentlayout.getChildAt(position);
                    clockEdit = (ImageView) layout.findViewById(R.id.clock_item_img_edit);
                    clockEdit.setBackgroundResource(R.drawable.ic_edit_landscape);
                    deleteBtn = (Button) layout.findViewById(R.id.alarm_item_img_delete);
                    deleteBtn.setVisibility(View.GONE);
                    imgeArrow = (ImageView) layout.findViewById(R.id.alarm_item_img_arrow);
                    imgeArrow.setVisibility(View.VISIBLE);
                }
                isDelete = true;
            }

        }
    };

    OnClickListener deleteClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Alarm alarm = worldAlarmList.get(position);
            if (alarm.timezone.equals(getResources().getString(R.string.timezone))) {
                ShareFileUtils.setBoolean("init_native_clock", true);
            }

            worldAlarmList.remove(position);
            contentlayout.removeViewAt(position);
            int childrenCount = contentlayout.getChildCount();

            // contentlayout.getChildAt(position).setVisibility(View.GONE);
            Alarms.deleteWorldClockCursor(WorldClock.this, id);
            isDelete = true;

            RelativeLayout layout;
            for (int i = 0; i < childrenCount; i++) {
                layout = (RelativeLayout) contentlayout.getChildAt(i);
                clockEdit = (ImageView) layout.findViewById(R.id.clock_item_img_edit);
                clockEdit.setOnClickListener(editClick);
                clockEdit.setTag(i);
            }
            if (worldAlarmList.size() == 0) {
                isDelete = false;
                isState = true;
                editBtn.setBackgroundResource(R.drawable.ic_button_1);
                editBtn.setText(getResources().getString(R.string.edit));
                editBtn.setEnabled(false);
                setNoWorldClockTextView();
            }

            if (worldAlarmList.size() < 1 || worldAlarmList.size() >= 3) {
                mTopBg.setVisibility(View.GONE);
            } else {
                mTopBg.setVisibility(View.VISIBLE);
            }
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            editBtn.setBackgroundResource(R.drawable.ic_button_1);
            editBtn.setText(getResources().getString(R.string.edit));
            Alarm alarm = new Alarm(this);
            Bundle bundle = data.getExtras();
            String country = bundle.getString("country");

            if (isSameCountry(country)) {
                alarm.timezone = bundle.getString("keyId");
                alarm.country = bundle.getString("country");
                Alarms.addWorldClockCursor(this, alarm);

                mCursor = Alarms.getWorldAlarmsCursor(getContentResolver());

                worldAlarmList = getAlarmList(mCursor);
                initView();
            }
            setNoWorldClockTextView();
        }
    }

    public boolean isSameCountry(String country) {

        for (int i = 0; i < worldAlarmList.size(); i++) {
            String tempCountry = worldAlarmList.get(i).country;
            if (country.equals(tempCountry)) {
                return false;
            }
        }
        return true;
    }


    public ArrayList<Alarm> getAlarmList(Cursor cursor) {
        ArrayList<Alarm> alarmList = new ArrayList<Alarm>();
        HashMap<String, Object> countries = SearchWroldClock.getCountries(this);
        String countryid;
        String country;

        for (int i = 0; i < cursor.getCount(); i++) {
            Alarm alarm = new Alarm(this);
            cursor.moveToPosition(i);
            alarm.id = cursor.getInt(Columns.ALARM_ID_INDEX);
            countryid = cursor.getString(cursor.getColumnIndex("country"));
            // System.out.println("pandy: countryid = " + countryid);
            // pandengyang
            country = (String) countries.get(countryid);
            if (country == null) {
                alarm.country = countryid;
                // System.out.println("pandy: countryid = " + countryid + "= null!");
            } else {
                if (country.contains("(")) {
                    country = country.substring(country.indexOf("(") + 1, country.lastIndexOf(")"));
                }
                alarm.country = country;
                // System.out.println("pandy: countryid = " + countryid + "=" + country);
            }

            alarm.timezone = cursor.getString(cursor.getColumnIndex("timezone"));
            alarmList.add(alarm);
        }


        return alarmList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetClock();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void setNoWorldClockTextView() {
        if (worldAlarmList != null && worldAlarmList.size() == 0) {
            mNoWorldClock.setVisibility(View.VISIBLE);
        } else {
            mNoWorldClock.setVisibility(View.GONE);
        }
    }

    public void initNativeClock() {
        Alarm alarm = new Alarm(this);
        alarm.timezone = getResources().getString(R.string.timezone);
        alarm.country = getResources().getString(R.string.time_zone_name_id);
        Alarms.addWorldClockCursor(this, alarm);
        ShareFileUtils.setBoolean("init_native_clock", true);
    }

    public void resetClock() {
        editBtn.setBackgroundResource(R.drawable.ic_button_1);
        editBtn.setText(getResources().getString(R.string.edit));
        editClock(false);
        isState = true;
        isDelete = true;
    }
}
