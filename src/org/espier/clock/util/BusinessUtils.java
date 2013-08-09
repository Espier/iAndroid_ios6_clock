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

package org.espier.clock.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.espier.clock.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;

public class BusinessUtils {

    private static Typeface typeface;
    private static Typeface typefaceBold;

    public static String queryAudioName(Context context, Uri name) {
        String audioId;
        String uriName = name.toString();
        uriName = uriName.substring(uriName.lastIndexOf("/") + 1);
        Cursor cursor =
                context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int counter = cursor.getCount();
            for (int j = 0; j < counter; j++) {
                audioId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                if (uriName.equals(audioId)) {
                    uriName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(context, name);
                    uriName = ringtone.getTitle(context);
                    break;
                }
                cursor.moveToNext();

            }
            cursor.close();
        } else {
            try {
                Ringtone ringtone = RingtoneManager.getRingtone(context, name);
                uriName = ringtone.getTitle(context);
            } catch (Exception e) {
                return uriName;
            }
        }
        return uriName;
    }

    public static boolean getSystemTime_12_24(Context context) {
        if (DateFormat.is24HourFormat(context)) {
            return true;
        }
        return false;
    }

    public static String getTimeText(int hour, int minute) {
        String text;
        if (hour < 10 && minute < 10) {
            text = "0" + hour + ":" + "0" + minute;
        } else if (minute < 10) {
            text = hour + ":" + "0" + minute;
        } else if (hour < 10) {
            text = "0" + hour + ":" + minute;
        } else {
            text = hour + ":" + minute;
        }
        return text;
    }

    public static String getTimeText(int hour, int minute, int ap) {
        String text;
        if (hour > 12) {
            hour -= 12;
        }
        if (hour < 10 && minute < 10) {
            text = "0" + hour + ":" + "0" + minute;
        } else if (minute < 10) {
            text = hour + ":" + "0" + minute;
        } else if (hour < 10) {
            text = "0" + hour + ":" + minute;
        } else {
            text = hour + ":" + minute;
        }

        return text;
    }

    public void getTime() {

    }

    public static String getDay(Context context, String timeZone) {

        // System data
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
        String defaultData = sd.format(now.getTime());
        // //timeZone data
        now.setTimeZone(TimeZone.getTimeZone(timeZone));
        sd.setTimeZone(TimeZone.getTimeZone(timeZone));
        String timeData = sd.format(now.getTime());

        String[] defaultDataArray = defaultData.split("/");
        String[] timeDataArray = timeData.split("/");

        int defaultDay = Integer.parseInt(defaultDataArray[2]);

        int timeDay = Integer.parseInt(timeDataArray[2]);

        if (defaultDay < timeDay) {
            return context.getString(R.string.tomorrow);
        } else if (defaultDay > timeDay) {
            return context.getString(R.string.yesterday);
        } else {
            return context.getString(R.string.today);
        }
    }

    public static String getDate() {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
        Date dd = new Date();
        return ft.format(dd);
    }

    public static long getQuot(String time1, String time2) {
        long quot = 0;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date1 = ft.parse(time1);
            Date date2 = ft.parse(time2);
            quot = date1.getTime() - date2.getTime();
            quot = quot / 1000 / 60 / 60 / 24;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return quot;
    }

    public static long getCountDay(String date1) {
        String date2 = getDate();
        long day = getQuot(date1, date2);
        return Math.abs(day);
    }

    public static boolean appIsInstalled(Context context, String pageName) {
        try {
            context.getPackageManager().getPackageInfo(pageName, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static float getDensity(Activity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.density;
    }

    public static boolean isAccessNetwork(Context context) {
        boolean result;
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connManager.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isAvailable()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public static Typeface getTypeface(Context context) {
        if (typeface == null) {
            typeface =
                    Typeface.createFromAsset(context.getApplicationContext().getAssets(),
                            "Helvetica.ttf");
        }
        return typeface;
    }

    public static Typeface getBoldTypeface(Context context) {
        if (typefaceBold == null) {
            typefaceBold =
                    Typeface.createFromAsset(context.getApplicationContext().getAssets(),
                            "Helvetica_Bold.ttf");
        }
        return typefaceBold;
    }
}
