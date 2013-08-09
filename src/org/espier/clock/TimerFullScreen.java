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

import org.espier.clock.broadcast.TimerAlarmService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Alarm Clock alarm alert: pops visible indicator and plays alarm tone. This activity is the full
 * screen version which shows over the lock screen with the wallpaper as the background.
 */
public class TimerFullScreen extends Activity {


    // These defaults must match the values in res/xml/settings.xml
    protected static final String SCREEN_OFF = "screen_off";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);

        final Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        updateLayout();

    }

    private void setTitle() {
        TextView title = (TextView) findViewById(R.id.alertTitle);
        title.setText(getResources().getString(R.string.dialog_message));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        dismiss(false);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateLayout() {
        LayoutInflater inflater = LayoutInflater.from(this);

        setContentView(inflater.inflate(R.layout.alarm_alert, null));

        View view = findViewById(R.id.line);
        view.setVisibility(View.GONE);
        Button snooze = (Button) findViewById(R.id.snooze);
        snooze.setVisibility(View.GONE);

        /* dismiss button: close notification */
        Button button = (Button) findViewById(R.id.dismiss);
        button.setText(getString(R.string.ok));
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                dismiss(false);
            }
        });

        /* Set the title from the passed in alarm */
        setTitle();
    }



    // Dismiss the alarm.
    public void dismiss(boolean killed) {
        stopService(new Intent(this, TimerAlarmService.class));
        finish();
    }

    /**
     * this is called when a second alarm is triggered while a previous alert window is still
     * active.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setTitle();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Log.LOGV) Log.v("AlarmAlert.onDestroy()");

    }

    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss. This method is overriden by AlarmAlert
        // so that the dialog is dismissed.
        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss(false);
            return true;
        }
        return false;
    }
}
