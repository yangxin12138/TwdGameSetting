package com.twd.gamesetting.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.twd.gamesetting.R;

public class SoundHelper {
    private static SoundHelper instance;
    private SoundPool soundPool;
    private final Context appContext;

    public int soundSelect = 0;
    public int soundConfirm = 0;

    private SoundHelper(Context context) {
        appContext = context.getApplicationContext();
        initPool();
    }

    // 初始化/重建SoundPool + 加载音频
    private void initPool() {
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(attr)
                .build();

        soundSelect = soundPool.load(appContext, R.raw.select, 1);
        soundConfirm = soundPool.load(appContext, R.raw.checksound, 1);
    }

    public static SoundHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SoundHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void playSelect() {
        // 先判断全局音效开关
        if (!GlobalSoundSwitchUtil.isSoundEffectOpen(appContext.getContentResolver())) {
            return;
        }
        // 容错：pool为空 / 音频ID异常 → 重建
        if (soundPool == null || soundSelect <= 0) {
            initPool();
        }
        soundPool.play(soundSelect, 0.8f, 0.8f, 1, 0, 1f);
    }

    public void playConfirm() {
        if (!GlobalSoundSwitchUtil.isSoundEffectOpen(appContext.getContentResolver())) {
            return;
        }
        if (soundPool == null || soundConfirm <= 0) {
            initPool();
        }
        soundPool.play(soundConfirm, 0.8f, 0.8f, 1, 0, 1f);
    }

    // 只在APP完全退出时调用，页面跳转不要调用！
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        // 不要置空 instance
    }
}