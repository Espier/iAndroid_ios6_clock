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
import java.util.HashMap;

import org.espier.clock.Alarm;
import org.espier.clock.R;
import org.espier.clock.util.BusinessUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlarmListAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<Alarm> alarmList;
    private Alarm alarm;
    private boolean isEdit;
    private boolean isDelete;
    private int deletePosition = -1;
    public HashMap<Integer, Boolean> map;
    private Typeface mBoldTypeface;
    private Typeface mTypeface;

    public AlarmListAdapter(Context context, ArrayList<Alarm> alarmList) {
        this.context = (Activity) context;
        this.alarmList = alarmList;
        map = new HashMap<Integer, Boolean>();
        for (int i = 0; i < alarmList.size(); i++) {
            map.put(i, false);
        }
        mBoldTypeface = BusinessUtils.getBoldTypeface(context.getApplicationContext());
        mTypeface = BusinessUtils.getTypeface(context.getApplicationContext());
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.alarm_list_item, null);
            viewHolder.imgeEdit = (ImageView) convertView.findViewById(R.id.alarm_item_img_edit);
            viewHolder.imgeCheckon =
                    (ImageView) convertView.findViewById(R.id.alarm_item_img_checkon);
            viewHolder.alarmTime = (TextView) convertView.findViewById(R.id.alarm_item_time);
            viewHolder.alarmLabel = (TextView) convertView.findViewById(R.id.alarm_item_name);
            viewHolder.alarmDelete = (Button) convertView.findViewById(R.id.alarm_item_img_delete);
            viewHolder.imgeArrow = (ImageView) convertView.findViewById(R.id.alarm_item_img_arrow);
            viewHolder.layout =
                    (LinearLayout) convertView.findViewById(R.id.alarm_item_edit_layout);
            viewHolder.mAmPm = (TextView) convertView.findViewById(R.id.alarm_am_pm);
            viewHolder.alarmTime.setTypeface(mBoldTypeface);
            viewHolder.alarmLabel.setTypeface(mTypeface);
            viewHolder.mAmPm.setTypeface(mTypeface);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        alarm = alarmList.get(position);
        viewHolder.alarmLabel.setText(alarm.label);

        if (BusinessUtils.getSystemTime_12_24(context)) {
            viewHolder.mAmPm.setVisibility(View.GONE);
            viewHolder.alarmTime.setText(BusinessUtils.getTimeText(alarm.hour, alarm.minutes));
        } else {
            viewHolder.mAmPm.setVisibility(View.VISIBLE);
            if (alarm.interval == 0 && alarm.hour > 12) {
                viewHolder.mAmPm.setText(context.getString(R.string.pm_text));
            } else if (alarm.interval == 1 && alarm.hour <= 12) {
                viewHolder.mAmPm.setText(context.getString(R.string.am_text));
            } else if (alarm.interval == 0 && alarm.hour <= 12) {
                viewHolder.mAmPm.setText(context.getString(R.string.am_text));
            } else if (alarm.interval == 1 && alarm.hour > 12) {
                viewHolder.mAmPm.setText(context.getString(R.string.pm_text));
            }
            viewHolder.alarmTime.setText(BusinessUtils.getTimeText(alarm.hour, alarm.minutes,
                    alarm.interval));

        }


        // 编辑状态
        if (isEdit) {
            viewHolder.imgeCheckon.setVisibility(View.GONE);
            viewHolder.imgeEdit.setVisibility(View.VISIBLE);
            viewHolder.imgeArrow.setVisibility(View.VISIBLE);
            viewHolder.layout.setVisibility(View.VISIBLE);

        } else {
            viewHolder.imgeCheckon.setVisibility(View.VISIBLE);
            viewHolder.imgeEdit.setVisibility(View.GONE);
            viewHolder.imgeArrow.setVisibility(View.GONE);
            viewHolder.alarmDelete.setVisibility(View.GONE);
            viewHolder.layout.setVisibility(View.GONE);

            viewHolder.imgeCheckon.setTag(position);
            viewHolder.imgeCheckon.setOnTouchListener((OnTouchListener) context);

        }


        if (isEdit && map.get(position)) {
            viewHolder.alarmDelete.setVisibility(View.VISIBLE);
            viewHolder.imgeArrow.setVisibility(View.GONE);
            viewHolder.imgeEdit.setBackgroundResource(R.drawable.ic_edit_portrait);

            viewHolder.alarmDelete.setTag(position);
            viewHolder.alarmDelete.setOnTouchListener((OnTouchListener) context);
        } else {
            viewHolder.alarmDelete.setVisibility(View.GONE);
            viewHolder.imgeEdit.setBackgroundResource(R.drawable.ic_edit_landscape);

            if (isEdit) {
                viewHolder.imgeArrow.setVisibility(View.VISIBLE);
            }
        }

        if (alarm.enabled) {
            viewHolder.imgeCheckon.setBackgroundResource(R.drawable.ic_checkon);
        } else {
            viewHolder.imgeCheckon.setBackgroundResource(R.drawable.ic_checkoff);
        }

        viewHolder.imgeEdit.setTag(position);
        viewHolder.imgeEdit.setOnTouchListener((OnTouchListener) context);
        convertView.setTag(viewHolder);

        return convertView;
    }


    class ViewHolder {
        LinearLayout layout;
        ImageView imgeEdit;
        ImageView imgeCheckon;
        ImageView imgeArrow;
        TextView alarmTime;
        TextView alarmLabel;
        TextView mAmPm;
        Button alarmDelete;
    }

}
