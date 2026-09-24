package com.twd.gamesetting;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class BrightnessActivity extends AppCompatActivity {
    // 亮度范围 50~100，步长10
    private final int MIN_BRIGHT = 10;
    private final int MAX_BRIGHT = 100;
    private final int STEP = 10;

    private static final String[] BRIGHTNESS_PERCENT = {
            "10%", "20%", "30%", "40%", "50%",
            "60%", "70%", "80%", "90%", "100%"
    };

    private static final int[] BRIGHTNESS_VALUES = {
            10, 20, 30, 40, 50, 60, 70, 80, 90, 100
    };

    private ListView lvBrightness;
    private BrightnessItemAdapter adapter;
    private final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brightness);

        lvBrightness = findViewById(R.id.lv_brightness);
        adapter = new BrightnessItemAdapter(context, BRIGHTNESS_PERCENT);
        lvBrightness.setAdapter(adapter);

        int curPercent = readCurrentBrightnessPercent();
        int curIndex = percentToIndex(curPercent);
        lvBrightness.post(() -> lvBrightness.setSelection(curIndex));
        adapter.setFocusedItem(curIndex);

        // OK 键确认选择
        lvBrightness.setOnItemClickListener((parent, view, position, id) -> {
            setBrightness(BRIGHTNESS_VALUES[position]);
            Log.d("yangxin", "onCreate: 亮度调节点击OK键");
            finish();
        });

        lvBrightness.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
                    int position = lvBrightness.getSelectedItemPosition();
                    if (position >= 0) {
                        setBrightness(BRIGHTNESS_VALUES[position]);
                        Log.d("yangxin", "OK键确认亮度: " + BRIGHTNESS_VALUES[position]);
                        finish();
                    }
                    return true; // 消费事件
                }
            }
            return false;
        });

        // 焦点变化
        lvBrightness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setFocusedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /** 读取系统亮度并转成百分比，限制到 10~100 */
    private int readCurrentBrightnessPercent() {
        ContentResolver cr = getContentResolver();
        int sysBright;
        try {
            sysBright = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            sysBright = 128;
        }
        int percent = Math.round(sysBright * 100f / 255f);
        return Math.max(MIN_BRIGHT, Math.min(MAX_BRIGHT, percent));
    }

    /** 百分比转成列表下标（就近取整到 10 的倍数） */
    private int percentToIndex(int percent) {
        int index = Math.round(percent / (float) STEP) - 1;
        if (index < 0) index = 0;
        if (index >= BRIGHTNESS_VALUES.length) index = BRIGHTNESS_VALUES.length - 1;
        return index;
    }

    /** 设置亮度：百分比转 0~255 并写入系统 */
    private void setBrightness(int percent) {
        int sysVal = Math.round(percent * 255f / 100f);
        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, sysVal);

        // 当前页面实时生效
        android.view.WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = sysVal / 255f;
        getWindow().setAttributes(lp);
    }
    class BrightnessItemAdapter extends ArrayAdapter<String> {
        private final LayoutInflater inflater;
        private int focusedItem = 0;

        public BrightnessItemAdapter(@NonNull Context context, String[] objects) {
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