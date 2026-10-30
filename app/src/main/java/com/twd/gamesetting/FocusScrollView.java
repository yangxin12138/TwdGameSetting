package com.twd.gamesetting;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ScrollView;

public class FocusScrollView extends ScrollView {
    private static final String TAG = "FocusScrollView";
    private int mVerticalHeldKey = 0; // 0 表示没有垂直键按住

    public FocusScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                mVerticalHeldKey = keyCode;
            } else if (event.getAction() == KeyEvent.ACTION_UP && mVerticalHeldKey == keyCode) {
                mVerticalHeldKey = 0;
            }
            return false; // 放行，保留系统 repeat 和音效
        }
        // 水平键：垂直键按住期间，让 focusSearch 不产生横向移动
        return super.dispatchKeyEvent(event);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        if (mVerticalHeldKey != 0 && (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT)) {
            // 垂直键按住时，水平方向的焦点搜索返回自身，等于不动
            return focused;
        }
        return super.focusSearch(focused, direction);
    }
}
