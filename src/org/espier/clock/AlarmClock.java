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

import org.espier.clock.adapter.AlarmListAdapter;
import org.espier.clock.util.BusinessUtils;
import org.espier.clock.util.Constant;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class AlarmClock extends Activity implements OnTouchListener {

    public static final String TAG = AlarmClock.class.getSimpleName();

    public static final String PREFERENCES = "AlarmClock";
    private Button editAlarmBtn;
    private Button addAlarmBtn;
    private ListView listView;
    private ArrayList<Alarm> alarmList;
    private AlarmListAdapter alarmAdapter;
    private int position;
    private boolean isState = true;
    private boolean isDelete = true;
    private Cursor mCursor;
    private Alarm alarm;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        Constant.density = BusinessUtils.getDensity(this);
        editAlarmBtn = (Button) findViewById(R.id.edit_btn);
        addAlarmBtn = (Button) findViewById(R.id.add_btn);
        listView = (ListView) findViewById(R.id.alarm_list);

        mCursor = Alarms.getAlarmsCursor(getContentResolver());
        alarmList = getTimeList(mCursor);

        if (alarmList.size() == 0) {
            editAlarmBtn.setEnabled(false);
        } else {
            editAlarmBtn.setEnabled(true);
        }

        alarmAdapter = new AlarmListAdapter(this, alarmList);

        listView.setAdapter(alarmAdapter);


        editAlarmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isState) {
                    editAlarmBtn.setBackgroundResource(R.drawable.ic_button_2);
                    editAlarmBtn.setText(getResources().getString(R.string.finish));
                    alarmAdapter.notifyDataSetChanged();
                    alarmAdapter.setEdit(true);
                    isState = false;
                } else {
                    editAlarmBtn.setBackgroundResource(R.drawable.ic_button_1);
                    editAlarmBtn.setText(getResources().getString(R.string.edit));
                    resetMapValue();
                    alarmAdapter.notifyDataSetChanged();
                    alarmAdapter.setEdit(false);
                    alarmAdapter.setDelete(false);
                    isState = true;
                    isDelete = true;
                }
            }
        });

        addAlarmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isState) {
                    Intent intent = new Intent(AlarmClock.this, SetAlarmClock.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (!isState) {
                    Intent intent = new Intent(AlarmClock.this, SetAlarmClock.class);
                    intent.putExtra(Alarms.ALARM_ID, alarmList.get(arg2).id);
                    startActivityForResult(intent, 1);
                }
            }
        });


        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ALARMS_ACTION_CHANGE);
        registerReceiver(mAlarmChange, filter);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int viewId = v.getId();
        int action = event.getAction();
        System.out.println(viewId + " " + v.toString());
        switch (viewId) {
            case R.id.alarm_item_img_checkon:
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        position = (Integer) v.findViewById(R.id.alarm_item_img_checkon).getTag();
                        if (alarmList.get(position).enabled) {
                            v.setBackgroundResource(R.drawable.ic_checkoff);
                            alarmList.get(position).enabled = false;
                            updateCheckOnAlarm(false, alarmList.get(position).id);
                        } else {
                            v.setBackgroundResource(R.drawable.ic_checkon);
                            alarmList.get(position).enabled = true;
                            updateCheckOnAlarm(true, alarmList.get(position).id);
                        }
                        break;
                }
                break;
            case R.id.alarm_item_img_edit:
                System.out.println(" isDelete  " + isDelete + "  position " + position);
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        position = (Integer) v.findViewById(R.id.alarm_item_img_edit).getTag();
                        setMapValue(position);
                        alarmAdapter.notifyDataSetChanged();
                        break;
                }
                break;

            case R.id.alarm_item_img_delete:
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundResource(R.drawable.ic_delete_down);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundResource(R.drawable.ic_delete_up);
                        position = (Integer) v.findViewById(R.id.alarm_item_img_delete).getTag();
                        deleteItem(position);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.setBackgroundResource(R.drawable.ic_delete_up);
                        break;
                }
                break;
        }
        return true;
    }

    public void deleteItem(int position) {
        Alarms.deleteAlarm(this, alarmList.get(position).id);
        alarmList.remove(position);
        alarmAdapter.map.remove(position);
        resetMapValue();
        alarmAdapter.notifyDataSetChanged();
        if (alarmList.size() == 0) {
            editAlarmBtn.setBackgroundResource(R.drawable.ic_button_1);
            editAlarmBtn.setText(getResources().getString(R.string.edit));
            editAlarmBtn.setEnabled(false);
            isState = true;
        }
    }

    public ArrayList<Alarm> getTimeList(Cursor cursor) {
        ArrayList<Alarm> timeList = new ArrayList<Alarm>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            alarm = new Alarm(cursor);
            timeList.add(alarm);
        }
        return timeList;
    }

    private void updateCheckOnAlarm(boolean enabled, int id) {
        Alarms.enableAlarm(this, id, enabled);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refresh();
    };

    public void refresh() {
        mCursor = Alarms.getAlarmsCursor(getContentResolver());
        alarmList = getTimeList(mCursor);
        alarmAdapter = new AlarmListAdapter(this, alarmList);
        listView.setAdapter(alarmAdapter);
        editAlarmBtn.setBackgroundResource(R.drawable.ic_button_1);
        editAlarmBtn.setText(getString(R.string.edit));
        isState = true;
        if (alarmList.size() == 0) {
            editAlarmBtn.setEnabled(false);
        } else {
            editAlarmBtn.setEnabled(true);
        }
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


    public void setMapValue(int position) {
        int size = alarmList.size();
        boolean isVisable = false;
        for (int i = 0; i < size; i++) {
            if (alarmAdapter.map.get(i)) {
                isVisable = true;
                alarmAdapter.map.put(i, false);
            }
        }

        if (!isVisable) {
            alarmAdapter.map.put(position, true);
        }

    }

    public void resetMapValue() {
        int size = alarmList.size();
        for (int i = 0; i < size; i++) {
            alarmAdapter.map.put(i, false);
        }
    }

    BroadcastReceiver mAlarmChange = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.ALARMS_ACTION_CHANGE.equals(intent.getAction())) {
                refresh();
            }
        }
    };


    public void resetClock() {
        editAlarmBtn.setBackgroundResource(R.drawable.ic_button_1);
        editAlarmBtn.setText(getResources().getString(R.string.edit));
        resetMapValue();
        alarmAdapter.notifyDataSetChanged();
        alarmAdapter.setEdit(false);
        alarmAdapter.setDelete(false);
        isState = true;
        isDelete = true;
    }

}
