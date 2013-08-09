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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParserException;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SearchWroldClock extends Activity {

    private Button searchCancle;
    private ImageView searchFork;
    private ListView searchList;
    private EditText searchEdit;

    private static final String TAG = "ZonePicker";

    // pandengyang
    private static final String COUNTRY_ID = "countryid"; // value: String
    private static final String KEY_ID = "id"; // value: String
    private static final String KEY_DISPLAYNAME = "name"; // value: String
    private static final String KEY_GMT = "gmt"; // value: String
    private static final String KEY_OFFSET = "offset"; // value: int (Integer)
    private static final String XMLTAG_TIMEZONE = "timezone";

    private static final int HOURS_1 = 60 * 60000;


    private ArrayList<HashMap<String, Object>> tempList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.searchworldclock);

        searchCancle = (Button) findViewById(R.id.search_cancel);
        searchFork = (ImageView) findViewById(R.id.search_fork);
        searchList = (ListView) findViewById(R.id.search_wrold_clock_list);
        searchEdit = (EditText) findViewById(R.id.search_edit);

        tempList = new ArrayList<HashMap<String, Object>>();
        tempList = (ArrayList<HashMap<String, Object>>) getZones(this);
        searchList.setAdapter(constructTimezoneAdapter(this, true));

        searchFork.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                searchEdit.setText("");
            }
        });

        searchCancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideInputMethod();
                finish();
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();

                if (text.contains(" ")) {
                    return;
                }
                setAdapter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                HashMap<String, Object> map = (HashMap<String, Object>) tempList.get(arg2);
                setResultData(map);

            }
        });
        closeInputMethod();
    }

    public void setResultData(HashMap<String, Object> map) {

        String tzId = (String) map.get(KEY_ID);
        // String country = (String)map.get(KEY_DISPLAYNAME);//old
        // pandengyang
        String country = (String) map.get(COUNTRY_ID);// new

        // Time
        Calendar now = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone(tzId);
        now.setTimeZone(timeZone);
        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        // country
        if (country.contains("(")) {
            country = country.substring(country.indexOf("(") + 1, country.lastIndexOf(")"));
        }

        Intent intent = new Intent(this, WorldClock.class);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);
        intent.putExtra("second", second);
        intent.putExtra("keyId", tzId);
        intent.putExtra("country", country);
        setResult(1, intent);
        finish();


    }


    public void setAdapter(String s) {

        List<HashMap<String, Object>> sortedList = getZones(this);
        // System.out.println("sortedList size "+sortedList.size());
        tempList.clear();
        if (!s.equals("")) {
            for (int i = 0; i < sortedList.size(); i++) {
                HashMap<String, Object> map = (HashMap<String, Object>) sortedList.get(i);
                String name = (String) map.get(KEY_DISPLAYNAME);
                // System.out.println("name : "+name+"  s: "+s);
                if (name.contains(s)) {
                    tempList.add(map);
                }
            }
        } else {
            tempList = (ArrayList<HashMap<String, Object>>) getZones(this);
        }
        searchList.setAdapter(constructTimezoneAdapter(this, true));
    }

    /**
     * Constructs an adapter with TimeZone list. Sorted by TimeZone in default.
     * 
     * @param sortedByName use Name for sorting the list.
     */
    public SimpleAdapter constructTimezoneAdapter(Context context, boolean sortedByName) {
        return constructTimezoneAdapter(context, sortedByName, R.layout.search_world_item);
    }

    /**
     * Constructs an adapter with TimeZone list. Sorted by TimeZone in default.
     * 
     * @param sortedByName use Name for sorting the list.
     */
    public SimpleAdapter constructTimezoneAdapter(Context context, boolean sortedByName,
            int layoutId) {
        final String[] from = new String[] {KEY_DISPLAYNAME, KEY_GMT};
        final int[] to = new int[] {R.id.text1, R.id.text2};

        final String sortKey = (sortedByName ? KEY_DISPLAYNAME : KEY_OFFSET);
        final MyComparator comparator = new MyComparator(sortKey);
        // final List<HashMap<String, Object>> sortedList = getZones(context);
        Collections.sort(tempList, comparator);
        final SimpleAdapter adapter = new SimpleAdapter(context, tempList, layoutId, from, to);
        return adapter;
    }

    // pandengyang
    static HashMap<String, Object> getCountries(Context context) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);
            while (xrp.next() != XmlResourceParser.START_TAG)
                continue;
            xrp.next();
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return map;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
                    String countryid = xrp.getAttributeValue(0);
                    String country = xrp.nextText();
                    map.put(countryid, country);
                    // System.out.println("pandy: " + countryid + ":" + country);
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
            xrp.close();
        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "Ill-formatted timezones.xml file");
        } catch (java.io.IOException ioe) {
            Log.e(TAG, "Unable to read timezones.xml file");
        }
        return map;
    }

    private static List<HashMap<String, Object>> getZones(Context context) {
        final List<HashMap<String, Object>> myData = new ArrayList<HashMap<String, Object>>();
        final long date = Calendar.getInstance().getTimeInMillis();
        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);
            while (xrp.next() != XmlResourceParser.START_TAG)
                continue;
            xrp.next();
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return myData;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
                    // pandengyang
                    String countryid = xrp.getAttributeValue(0);
                    String id = xrp.getAttributeValue(1);

                    String displayName = xrp.nextText();
                    addItem(myData, id, displayName, countryid, date);
                    // addItem(myData, id, displayName, date);//old pandengyang
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
            xrp.close();
        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "Ill-formatted timezones.xml file");
        } catch (java.io.IOException ioe) {
            Log.e(TAG, "Unable to read timezones.xml file");
        }

        return myData;
    }

    private static void addItem(List<HashMap<String, Object>> myData, String id,
            String displayName, String countryid, long date) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_ID, id);
        map.put(KEY_DISPLAYNAME, displayName);
        map.put(COUNTRY_ID, countryid);
        final TimeZone tz = TimeZone.getTimeZone(id);
        final int offset = tz.getOffset(date);
        final int p = Math.abs(offset);
        final StringBuilder name = new StringBuilder();
        name.append("GMT");

        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }

        name.append(p / (HOURS_1));
        name.append(':');

        int min = p / 60000;
        min %= 60;

        if (min < 10) {
            name.append('0');
        }
        name.append(min);

        map.put(KEY_GMT, name.toString());
        map.put(KEY_OFFSET, offset);

        myData.add(map);
    }

    private static class MyComparator implements Comparator<HashMap<?, ?>> {
        private String mSortingKey;

        public MyComparator(String sortingKey) {
            mSortingKey = sortingKey;
        }

        public int compare(HashMap<?, ?> map1, HashMap<?, ?> map2) {
            Object value1 = map1.get(mSortingKey);
            Object value2 = map2.get(mSortingKey);

            /*
             * This should never happen, but just in-case, put non-comparable items at the end.
             */
            if (!isComparable(value1)) {
                return isComparable(value2) ? 1 : 0;
            } else if (!isComparable(value2)) {
                return -1;
            }

            return ((Comparable) value1).compareTo(value2);
        }

        private boolean isComparable(Object value) {
            return (value != null) && (value instanceof Comparable);
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

    public void closeInputMethod() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (imm != null && imm.isActive() && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void hideInputMethod() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {} else {
            im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }
}
