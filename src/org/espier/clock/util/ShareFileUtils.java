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

import android.content.Context;
import android.content.SharedPreferences;
/**
 *
 * @author ZhangPeng
 * @version 1.0.0
 */
public class ShareFileUtils {

    private static SharedPreferences pref;

    public static String getString(String key, String defValue) {
        return pref.getString(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        return pref.getInt(key, defValue);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return pref.getBoolean(key, defValue);
    }

    public static void setString(String key, String defValue) {
        pref.edit().putString(key, defValue).commit();
    }

    public static void setInt(String key, int defValue) {
        pref.edit().putInt(key, defValue).commit();
    }

    public static void setBoolean(String key, boolean defValue) {
        pref.edit().putBoolean(key, defValue).commit();
    }

    public static void setContext(Context context) {
        pref = context.getSharedPreferences("espier_clock_recoder", 0); // 文件操作对象
    }

}
