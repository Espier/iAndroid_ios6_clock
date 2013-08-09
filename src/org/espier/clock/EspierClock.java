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

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class EspierClock extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;

        spec = createTabSpec(tabHost, WorldClock.TAG, res,
                R.string.world_clock, R.drawable.ic_tab_worldclock,
                WorldClock.class);
        tabHost.addTab(spec);
        spec = createTabSpec(tabHost, AlarmClock.TAG, res, R.string.alarm,
                R.drawable.ic_tab_alarm, AlarmClock.class);
        tabHost.addTab(spec);
        spec = createTabSpec(tabHost, Stopwatch.TAG, res, R.string.stopwatch,
                R.drawable.ic_tab_stopwatch, Stopwatch.class);
        tabHost.addTab(spec);
        spec = createTabSpec(tabHost, Timer.TAG, res, R.string.timer,
                R.drawable.ic_tab_timer, Timer.class);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }

    private TabHost.TabSpec createTabSpec(TabHost tabHost, String tag,
            Resources res, int labelId, int iconId, Class<?> cls) {
        TabHost.TabSpec spec = tabHost.newTabSpec(tag);
        String label = res.getString(labelId);
        Drawable icon = res.getDrawable(iconId);

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.tab, null);
        ((ImageView) linearLayout.findViewById(R.id.tab_icon))
                .setImageDrawable(icon);
        ((TextView) linearLayout.findViewById(R.id.tab_label)).setText(label);

        spec.setIndicator(linearLayout);
        spec.setContent(new Intent().setClass(this, cls));

        return spec;
    }
}
