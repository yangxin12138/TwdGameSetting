package com.twd.gamesetting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ScrollView;

public class FocusScrollView extends ScrollView {

    public FocusScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        // 方向键不拦截，交给系统做焦点切换（系统会顺带播按键音）
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            return false; // ← 关键：不消费
        }
        return super.dispatchKeyEvent(event);
    }
}
