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

import org.espier.clock.util.BusinessUtils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LapsAdapter extends BaseAdapter {

    private static final String HOLDERSTR = "";
    private static final int HOLDERCNT = 4;

    private Context mContext;
    private ArrayList<String> mLaps;
    private int mHoldersInList;
    private Typeface mTypeface;

    public LapsAdapter(Context context) {
        mContext = context;
        mLaps = new ArrayList<String>();
        mHoldersInList = 0;
        for (int i = 0; i < HOLDERCNT; i++) {
            mLaps.add(0, HOLDERSTR);
            mHoldersInList++;
        }
        mTypeface = BusinessUtils.getTypeface(context.getApplicationContext());
    }

    public void addLap(String lap) {
        String last = mLaps.get(mLaps.size() - 1);
        if (last.equals(HOLDERSTR)) {
            mLaps.remove(mLaps.size() - 1);
            mHoldersInList--;
        }
        mLaps.add(0, lap);
        notifyDataSetInvalidated();
    }

    public void clear() {
        mLaps.clear();
        mHoldersInList = 0;
        for (int i = 0; i < HOLDERCNT; i++) {
            mLaps.add(0, HOLDERSTR);
            mHoldersInList++;
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        return mLaps.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(
                    mContext).inflate(R.layout.laps, null, false);

            convertView = linearLayout;
            viewHolder.lapno = (TextView) convertView.findViewById(R.id.lapno);
            viewHolder.laptime = (TextView) convertView.findViewById(R.id.laptime);
            viewHolder.lapno.setTypeface(mTypeface);
            viewHolder.laptime.setTypeface(mTypeface);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String time = mLaps.get(position);

        if (time == null || time.equals(HOLDERSTR)) {
            viewHolder.lapno.setText(HOLDERSTR);
            viewHolder.laptime.setText(HOLDERSTR);
        } else {
            viewHolder.lapno.setText(mContext.getResources().getString(R.string.lap_prefix) + (mLaps.size() - mHoldersInList - position));
            viewHolder.laptime.setText(time);
        }

        return convertView;
    }

    class ViewHolder {
        TextView lapno;
        TextView laptime;
    }

}
