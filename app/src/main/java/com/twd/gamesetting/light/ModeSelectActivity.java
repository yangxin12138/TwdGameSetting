package com.twd.gamesetting.light;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.twd.gamesetting.R;
import com.twd.gamesetting.utils.SystemUtils;

public class ModeSelectActivity extends AppCompatActivity {

    private static final String JOYSTICK_LED_MODE  = "persist.mode.joystick.led";
    private static final String JOYSTICK_LED_COLOR = "persist.color.joystick.led";
    private static final String path_mode  = "/sys/leds/mode";
    private static final String path_color = "/sys/leds/color";

    private static final int MODE_RAINBOW_ROTATE = 16;

    private static final int[][] MODE_NODE_MAP = {
            {0, -1}, {1, 0}, {1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {1, 6},
            {2, 0}, {2, 1}, {2, 2}, {2, 3}, {2, 4}, {2, 5}, {2, 6},
            {3, -1}, {4, -1},
    };

    private LinearLayout llModeContainer;
    private String[] MODE_NAMES;
    private final View[] itemViews = new View[MODE_NODE_MAP.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);

        initModeNames();

        llModeContainer = findViewById(R.id.ll_mode_container);

        // 动态添加每个选项
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < MODE_NAMES.length; i++) {
            final int position = i;
            View item = inflater.inflate(R.layout.item_mode, llModeContainer, false);
            TextView tvName = item.findViewById(R.id.tv_name);
            tvName.setText(MODE_NAMES[i]);

            item.setOnClickListener(v -> {
                setModeValue(position);
                finish();
            });

            // 有些遥控器用 DPAD_CENTER 选中，有些用 ENTER；clickable + focusable 后
            // 系统会自动把 DPAD_CENTER 转成 click，这里不用额外监听。
            itemViews[i] = item;
            llModeContainer.addView(item);
        }

        // 初始焦点定位到当前模式项
        int kernelMode  = Integer.parseInt(SystemUtils.getProperty(JOYSTICK_LED_MODE, "0"));
        int kernelColor = Integer.parseInt(SystemUtils.getProperty(JOYSTICK_LED_COLOR, "-1"));
        int curPosition = modeKernelToIndex(kernelMode, kernelColor);

        View target = itemViews[curPosition];
        ScrollView sv = findViewById(R.id.sv_mode);
        int parentWidth  = getResources().getDisplayMetrics().widthPixels;
        int parentHeight = getResources().getDisplayMetrics().heightPixels;

        int widthSpec  = View.MeasureSpec.makeMeasureSpec(parentWidth,  View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parentHeight, View.MeasureSpec.AT_MOST);

        sv.measure(widthSpec, heightSpec);
        sv.layout(0, 0, sv.getMeasuredWidth(), sv.getMeasuredHeight());

        target.requestFocus();
        centerItemInScrollView(sv, target);
    }

    private void initModeNames() {
        MODE_NAMES = new String[]{
                getString(R.string.light_mode_turn_off),
                getString(R.string.light_mode_steady_on_Red),
                getString(R.string.light_mode_steady_on_Green),
                getString(R.string.light_mode_steady_on_Blue),
                getString(R.string.light_mode_steady_on_Purple),
                getString(R.string.light_mode_steady_on_Lightblue),
                getString(R.string.light_mode_steady_on_Yellow),
                getString(R.string.light_mode_steady_on_White),
                getString(R.string.light_mode_breath_Red),
                getString(R.string.light_mode_breath_Green),
                getString(R.string.light_mode_breath_Blue),
                getString(R.string.light_mode_breath_Purple),
                getString(R.string.light_mode_breath_Lightblue),
                getString(R.string.light_mode_breath_Yellow),
                getString(R.string.light_mode_breath_White),
                getString(R.string.light_mode_breath_ColorCycle),
                getString(R.string.light_mode_rotating_ColorCycle),
        };
    }

    private void setModeValue(int position) {
        if (position < 0 || position >= MODE_NODE_MAP.length) return;

        int[] nodes = MODE_NODE_MAP[position];
        int kernelMode  = nodes[0];
        int kernelColor = nodes[1];

        SystemUtils.setProperty(JOYSTICK_LED_MODE,  String.valueOf(kernelMode));
        SystemUtils.setProperty(JOYSTICK_LED_COLOR, String.valueOf(kernelColor));

        if (position == MODE_RAINBOW_ROTATE) {
            SystemUtils.writeSysNode(path_mode, 3);
            SystemUtils.writeSysNode(path_mode, 4);
        } else {
            SystemUtils.writeSysNode(path_mode, kernelMode);
            if (kernelColor >= 0) {
                SystemUtils.writeSysNode(path_color, kernelColor);
            }
        }
    }

    private int modeKernelToIndex(int kernelMode, int kernelColor) {
        for (int i = 0; i < MODE_NODE_MAP.length; i++) {
            int[] node = MODE_NODE_MAP[i];
            if (node[0] == kernelMode && node[1] == kernelColor) {
                return i;
            }
        }
        return 0;
    }

    private void centerItemInScrollView(ScrollView sv, View target) {
        int targetTop      = target.getTop();
        int targetHeight   = target.getHeight();
        int targetCenter   = targetTop + targetHeight / 2;

        int viewportHeight = sv.getHeight()
                - sv.getPaddingTop() - sv.getPaddingBottom();

        int targetScrollY  = targetCenter - viewportHeight / 2;

        int maxScroll = Math.max(0,
                llModeContainer.getHeight() - viewportHeight);
        targetScrollY = Math.max(0, Math.min(targetScrollY, maxScroll));

        sv.scrollTo(0, targetScrollY);
    }
}