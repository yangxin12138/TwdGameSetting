package com.twd.gamesetting.light;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.gamesetting.R;
import com.twd.gamesetting.utils.SystemUtils;

public class ModeSelectActivity extends AppCompatActivity {

    private static final String JOYSTICK_LED_MODE = "persist.sys.joystick.led";
    private static final String path_mode = "/sys/leds/mode";
    private static final String path_color = "/sys/leds/color";

    private String[] MODE_NAMES;

    // {path_mode, path_color}，path_color = -1 表示不写
    private static final int[][] MODE_NODE_MAP = {
            {0, -1}, // 0  关闭
            {1,  0}, // 1  长亮_红
            {1,  1}, // 2  长亮_黄
            {1,  2}, // 3  长亮_绿
            {1,  3}, // 4  长亮_天空蓝
            {1,  4}, // 5  长亮_蓝
            {1,  5}, // 6  长亮_紫
            {1,  6}, // 7  长亮_淡蓝
            {2,  0}, // 8  呼吸_红
            {2,  1}, // 9  呼吸_绿
            {2,  2}, // 10 呼吸_蓝
            {2,  3}, // 11 呼吸_紫
            {2,  4}, // 12 呼吸_天空蓝
            {2,  5}, // 13 呼吸_黄
            {2,  6}, // 14 呼吸_淡蓝
            {3, -1}, // 15 呼吸_变色循环
            {4, -1}, // 16 炫彩_旋转
    };

    private ListView lvMode;
    private ModeItemAdapter modeItemAdapter;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);
        String MODE_OFF = getResources().getString(R.string.light_mode_turn_off);
        String MODE_STATIC_RED = getResources().getString(R.string.light_mode_steady_on_Red);
        String MODE_STATIC_YELLOW = getResources().getString(R.string.light_mode_steady_on_Yellow);
        String MODE_STATIC_GREEN = getResources().getString(R.string.light_mode_steady_on_Green);
        String MODE_STATIC_SKYBLUE = getResources().getString(R.string.light_mode_steady_on_Skyblue);
        String MODE_STATIC_BLUE = getResources().getString(R.string.light_mode_steady_on_Blue);
        String MODE_STATIC_PURPLE = getResources().getString(R.string.light_mode_steady_on_Purple);
        String MODE_STATIC_LIGHTBLUE = getResources().getString(R.string.light_mode_steady_on_Lightblue);
        String MODE_BREATH_RED = getResources().getString(R.string.light_mode_breath_Red);
        String MODE_BREATH_YELLOW = getResources().getString(R.string.light_mode_breath_Yellow);
        String MODE_BREATH_GREEN = getResources().getString(R.string.light_mode_breath_Green);
        String MODE_BREATH_SKYBLUE = getResources().getString(R.string.light_mode_breath_Skyblue);
        String MODE_BREATH_BLUE = getResources().getString(R.string.light_mode_breath_Blue);
        String MODE_BREATH_PURPLE = getResources().getString(R.string.light_mode_breath_Purple);
        String MODE_BREATH_LIGHTBLUE = getResources().getString(R.string.light_mode_breath_Lightblue);
        String MODE_BREATH_COLORCIRCLE = getResources().getString(R.string.light_mode_breath_ColorCycle);
        String MODE_rotating_COLORCIRCLE = getResources().getString(R.string.light_mode_rotating_ColorCycle);
        MODE_NAMES = new String[]{
                MODE_OFF, MODE_STATIC_RED, MODE_STATIC_YELLOW, MODE_STATIC_GREEN, MODE_STATIC_SKYBLUE,
                MODE_STATIC_BLUE, MODE_STATIC_PURPLE, MODE_STATIC_LIGHTBLUE, MODE_BREATH_RED, MODE_BREATH_YELLOW,
                MODE_BREATH_GREEN, MODE_BREATH_SKYBLUE, MODE_BREATH_BLUE, MODE_BREATH_PURPLE, MODE_BREATH_LIGHTBLUE,
                MODE_BREATH_COLORCIRCLE, MODE_rotating_COLORCIRCLE
        };
        lvMode = findViewById(R.id.lv_mode);
        modeItemAdapter = new ModeItemAdapter(context, MODE_NAMES);
        lvMode.setAdapter(modeItemAdapter);

        int cur = Integer.parseInt(SystemUtils.getProperty(JOYSTICK_LED_MODE, "0"));
        lvMode.post(() -> lvMode.setSelection(cur));

        lvMode.setOnItemClickListener((parent, view, position, id) -> {
            setModeValue(position);
            finish();
        });

        lvMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modeItemAdapter.setFocusedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setModeValue(int value) {
        SystemUtils.setProperty(JOYSTICK_LED_MODE, String.valueOf(value));

        if (value < 0 || value >= MODE_NODE_MAP.length) return;

        int[] nodes = MODE_NODE_MAP[value];
        SystemUtils.writeSysNode(path_mode, nodes[0]);
        if (value == 16) {
            SystemUtils.writeSysNode(path_mode, 3); // 先切到呼吸变色循环
            SystemUtils.writeSysNode(path_mode, 4); // 再切到炫彩旋转
            return;
        }

        SystemUtils.writeSysNode(path_mode, nodes[0]);
        if (nodes[1] >= 0) {
            SystemUtils.writeSysNode(path_color, nodes[1]);
        }
    }

    class ModeItemAdapter extends ArrayAdapter<String> {
        private LayoutInflater inflater;
        private int focusedItem = 0;

        public ModeItemAdapter(@NonNull Context context, String[] objects) {
            super(context, 0, objects);
            inflater = LayoutInflater.from(context);
        }

        public void setFocusedItem(int position) {
            focusedItem = position;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = inflater.inflate(R.layout.item_mode, parent, false);
            }

            TextView tvName = itemView.findViewById(R.id.tv_name);
            tvName.setText(getItem(position));

            // 焦点效果
            if (position == focusedItem) {
                itemView.setBackgroundResource(R.color.bg_focus);
                tvName.setTextColor(context.getResources().getColor(R.color.tv_focus));
            } else {
                itemView.setBackgroundResource(R.color.bg_normal);
                tvName.setTextColor(context.getResources().getColor(R.color.tv_normal));
            }

            return itemView;
        }
    }
}
