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

package org.espier.clock.adapter;

import java.util.ArrayList;

import org.espier.clock.Alarm;
import org.espier.clock.R;
import org.espier.clock.util.BusinessUtils;
import org.espier.clock.widget.AnalogClock;
import org.espier.clock.widget.TimeView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WorldClockListAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<Alarm> alarmList;
    private Alarm alarm;
    private boolean isEdit;
    private boolean isDelete;
    private int deletePosition;



    public WorldClockListAdapter(Context context, ArrayList<Alarm> alarmList) {
        this.context = (Activity) context;
        this.alarmList = alarmList;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public void setDeletePosition(int deletePosition) {
        this.deletePosition = deletePosition;
    }

    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return alarmList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        RelativeLayout relativeLayout;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            relativeLayout =
                    (RelativeLayout) LayoutInflater.from(context).inflate(
                            R.layout.worldclock_list_item, null);

            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            alarm = alarmList.get(position);
            viewHolder.analogClock = new AnalogClock(context);
            viewHolder.analogClock.init(context, alarm.timezone);
            relativeLayout.addView(viewHolder.analogClock, params);

            convertView = relativeLayout;
            viewHolder.clockTimeLayout =
                    (LinearLayout) convertView.findViewById(R.id.clock_item_time_layout);

            LinearLayout.LayoutParams linearParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            viewHolder.clockTime = new TimeView(context);
            viewHolder.clockTime.init(alarm.timezone);
            viewHolder.clockTime.setTextColor(Color.BLACK);
            viewHolder.clockTime.setTextSize(20);
            viewHolder.clockTimeLayout.addView(viewHolder.clockTime, linearParams);

            viewHolder.textDay = new TextView(context);
            viewHolder.textDay.setTextColor(0xff666666);
            viewHolder.clockTimeLayout.addView(viewHolder.textDay, linearParams);


            viewHolder.clockCity = (TextView) convertView.findViewById(R.id.clock_item_city);
            viewHolder.clockDelete = (Button) convertView.findViewById(R.id.alarm_item_img_delete);
            viewHolder.imgeArrow = (ImageView) convertView.findViewById(R.id.alarm_item_img_arrow);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        alarm = alarmList.get(position);
        viewHolder.analogClock.init(context, alarm.timezone);

        viewHolder.clockCity.setText(alarm.country);
        viewHolder.textDay.setText(BusinessUtils.getDay(context, alarm.timezone));
        System.out.println("adpater :" + alarm.timezone);
        if (isEdit) {
            viewHolder.clockTimeLayout.setVisibility(View.GONE);
            viewHolder.imgeEdit.setVisibility(View.VISIBLE);
            viewHolder.imgeArrow.setVisibility(View.VISIBLE);

            viewHolder.imgeEdit.setTag(position);
            viewHolder.imgeEdit.setOnTouchListener((OnTouchListener) context);
        } else {
            viewHolder.clockTimeLayout.setVisibility(View.VISIBLE);
            viewHolder.imgeEdit.setVisibility(View.GONE);
            viewHolder.imgeArrow.setVisibility(View.GONE);
            viewHolder.clockDelete.setVisibility(View.GONE);


        }


        if (alarm.isDelete) {
            viewHolder.clockDelete.setVisibility(View.VISIBLE);
            viewHolder.imgeArrow.setVisibility(View.GONE);
            viewHolder.imgeEdit.setBackgroundResource(R.drawable.ic_edit_portrait);
            viewHolder.clockDelete.setBackgroundResource(R.drawable.ic_button_delete);

            viewHolder.clockDelete.setTag(position);
            viewHolder.clockDelete.setOnTouchListener((OnTouchListener) context);

        } else {
            viewHolder.clockDelete.setVisibility(View.GONE);
            viewHolder.imgeEdit.setBackgroundResource(R.drawable.ic_edit_landscape);
            if (isEdit) {
                viewHolder.imgeArrow.setVisibility(View.VISIBLE);
            }

        }
        convertView.setTag(viewHolder);

        return convertView;
    }


    class ViewHolder {
        ImageView imgeEdit;
        AnalogClock analogClock;
        // ImageView imgClock;
        ImageView imgeArrow;
        TimeView clockTime;
        TextView textDay;
        TextView clockCity;
        Button clockDelete;
        LinearLayout clockTimeLayout;
    }
}
