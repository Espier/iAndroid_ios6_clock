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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class AlarmProvider extends ContentProvider {
    private SQLiteOpenHelper mOpenHelper;

    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;

    private static final int WORLDALARMS = 3;
    private static final int WORLDALARMS_ID = 4;
    private static final UriMatcher sURLMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    private Uri newUrl;

    static {
        sURLMatcher.addURI("org.espier.clock", "alarm", ALARMS);
        sURLMatcher.addURI("org.espier.clock", "alarm/#", ALARMS_ID);
        sURLMatcher.addURI("org.espier.clock", "worldalarm", WORLDALARMS);
        sURLMatcher.addURI("org.espier.clock", "worldalarm/#", WORLDALARMS_ID);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "alarms.db";
        private static final int DATABASE_VERSION = 5;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE alarms (" +
                       "_id INTEGER PRIMARY KEY," +
                       "hour INTEGER, " +
                       "minutes INTEGER, " +
                       "daysofweek INTEGER, " +
                       "alarmtime INTEGER, " +
                       "enabled INTEGER, " +
                       "vibrate INTEGER, " +
                       "message TEXT, " +
                       "alert TEXT, " +
                       "times INTEGER, " +
                       "interval INTEGER," +
                       "soundname TEXT);");

            db.execSQL("CREATE TABLE worldalarms (" +
                    "_id INTEGER PRIMARY KEY," +
                    "country TEXT, " +
                    "timezone TEXT);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
            if (Log.LOGV) Log.v(
                    "Upgrading alarms database from version " +
                    oldVersion + " to " + currentVersion +
                    ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS alarms");
            db.execSQL("DROP TABLE IF EXISTS worldalarms");
            onCreate(db);
        }
    }

    public AlarmProvider() {
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection,
            String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Generate the body of the query
        int match = sURLMatcher.match(url);
        switch (match) {
            case ALARMS:
                qb.setTables("alarms");
                break;
            case ALARMS_ID:
                qb.setTables("alarms");
                qb.appendWhere("_id=");
                qb.appendWhere(url.getPathSegments().get(1));
                break;
            case WORLDALARMS:
                qb.setTables("worldalarms");
                break;
            case WORLDALARMS_ID:
                qb.setTables("worldalarms");
                qb.appendWhere("_id=");
                qb.appendWhere(url.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URL " + url);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projectionIn, selection, selectionArgs,
                              null, null, sort);

        if (ret == null) {
            if (Log.LOGV) Log.v("Alarms.query: failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), url);
        }

        return ret;
    }

    @Override
    public String getType(Uri url) {
        int match = sURLMatcher.match(url);
        switch (match) {
            case ALARMS:
                return "vnd.android.cursor.dir/alarms";
            case ALARMS_ID:
                return "vnd.android.cursor.item/alarms";
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public int update(Uri url, ContentValues values, String where, String[] whereArgs) {
        int count;
        long rowId = 0;
        int match = sURLMatcher.match(url);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match) {
            case ALARMS_ID: {
                String segment = url.getPathSegments().get(1);
                rowId = Long.parseLong(segment);
                count = db.update("alarms", values, "_id=" + rowId, null);
                break;

            }
            case WORLDALARMS_ID:{
                 String segment = url.getPathSegments().get(1);
                 rowId = Long.parseLong(segment);
                 count = db.update("worldalarms", values, "_id=" + rowId, null);
            }
            default: {
                throw new UnsupportedOperationException(
                        "Cannot update URL: " + url);
            }
        }
        if (Log.LOGV) Log.v("*** notifyChange() rowId: " + rowId + " url " + url);
        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {

        ContentValues values = new ContentValues(initialValues);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

     if(sURLMatcher.match(url) == ALARMS){
          long rowId = db.insert("alarms", Alarm.Columns.MESSAGE, values);
          if (rowId < 0) {
              throw new SQLException("Failed to insert row into " + url);
          }
          if (Log.LOGV) Log.v("Added alarm rowId = " + rowId);

          newUrl = ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, rowId);
     }else if(sURLMatcher.match(url) == WORLDALARMS){
          long rowId = db.insert("worldalarms", null, values);
          if (rowId < 0) {
              throw new SQLException("Failed to insert row into " + url);
          }
          if (Log.LOGV) Log.v("Added alarm rowId = " + rowId);

          newUrl = ContentUris.withAppendedId(Alarm.Columns.CONTENT_WORLD_URL, rowId);
     }

        getContext().getContentResolver().notifyChange(newUrl, null);
        return newUrl;
    }

    public int delete(Uri url, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sURLMatcher.match(url)) {
            case ALARMS:
                count = db.delete("alarms", where, whereArgs);
                break;
            case ALARMS_ID:
                String segment = url.getPathSegments().get(1);
                if (TextUtils.isEmpty(where)) {
                    where = "_id=" + segment;
                } else {
                    where = "_id=" + segment + " AND (" + where + ")";
                }
                count = db.delete("alarms", where, whereArgs);
                break;
            case WORLDALARMS:
                 count = db.delete("worldalarms", where, whereArgs);
                break;
            case WORLDALARMS_ID:
                String segments = url.getPathSegments().get(1);
                if (TextUtils.isEmpty(where)) {
                    where = "_id=" + segments;
                } else {
                    where = "_id=" + segments + " AND (" + where + ")";
                }
                count = db.delete("worldalarms", where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + url);
        }

        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }
}
