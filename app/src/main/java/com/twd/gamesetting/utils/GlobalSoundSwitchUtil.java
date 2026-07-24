package com.twd.gamesetting.utils;

import android.content.ContentResolver;
import android.provider.Settings;

public class GlobalSoundSwitchUtil {
    private static final String KEY_SOUND_EFFECT = "global_key_sound_effect";

    /**
     * 获取音效开关状态
     * @return true=开启音效，false=关闭；无数据默认true
     */
    public static boolean isSoundEffectOpen(ContentResolver cr) {
        try {
            int value = Settings.Global.getInt(cr, KEY_SOUND_EFFECT);
            return value == 1;
        } catch (Settings.SettingNotFoundException e) {
            // 不存在这条记录 → 默认开启音效
            return true;
        }
    }

    /**
     * 设置音效开关
     */
    public static void setSoundEffect(ContentResolver cr, boolean open) {
        int val = open ? 1 : 0;
        Settings.Global.putInt(cr, KEY_SOUND_EFFECT, val);
    }
}
