package com.georgeren.daily.utils;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.georgeren.daily.InitApp;
import com.georgeren.daily.R;

/**
 * Created by georgeRen on 2017/8/28.
 */

public class SettingUtil {
    private SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(InitApp.AppContext);
    public static SettingUtil getInstance(){
        return SettingsUtilInstance.instance;
    }

    private static class SettingsUtilInstance{
        private static SettingUtil instance = new SettingUtil();
    }

    /**
     * 获取模式：夜间、白天
     * @return
     */
    public boolean getIsNightMode(){
        return setting.getBoolean("switch_nightMode", false);
    }
    public void setIsNightMode(boolean flag) {
        setting.edit().putBoolean("switch_nightMode", flag).apply();
    }

    /**
     * 获取存储的主题颜色
     * @return
     */
    public int getColor() {
        int defaultColor = InitApp.AppContext.getResources().getColor(R.color.colorPrimary);
        int color = setting.getInt("color", defaultColor);
        if ((color != 0) && Color.alpha(color) != 255) {
            return defaultColor;
        }
        return color;
    }

    /**
     * 切换主题颜色后存储
     * @param color
     */
    public void setColor(int color) {
        setting.edit().putInt("color", color).apply();
    }




}
