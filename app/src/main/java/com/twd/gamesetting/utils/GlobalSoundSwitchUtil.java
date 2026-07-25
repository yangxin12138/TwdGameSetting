package com.twd.gamesetting.utils;

import android.content.ContentResolver;
import android.provider.Settings;

public class GlobalSoundSwitchUtil {

    /**
     * 获取音效开关状态
     * @return true=开启音效，false=关闭；无数据默认true
     */
    public static boolean isSoundEffectOpen(ContentResolver cr) {
        try {
            int value = Settings.System.getInt(cr, Settings.System.SOUND_EFFECTS_ENABLED);
            return value == 1;
        } catch (Settings.SettingNotFoundException e) {
            // 系统默认开启音效
            return false;
        }
    }

    /**
     * 设置音效开关
     */
    public static void setSoundEffect(ContentResolver cr, boolean open) {
        int val = open ? 1 : 0;
        String valStr = String.valueOf(val);
        Settings.System.putInt(cr, Settings.System.SOUND_EFFECTS_ENABLED, val);
        SystemUtils.setProperty("persist.sound.effect", valStr);
    }
}
