package com.twd.gamesetting.time;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.twd.gamesetting.LanguageBean;
import com.twd.gamesetting.R;
import com.twd.gamesetting.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class TimeZoneActivity extends AppCompatActivity {

    private static final String TAG = "TimeZoneActivity";
    public static final String EXTRA_TIME_ZONE_ID = "time_zone_id";

    private final Context context = this;
    private List<TimeZoneBean> timeZoneBeans = new ArrayList<>();
    private ListView listView;
    private TimeZoneItemAdapter timeZoneItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_zone);
        listView = findViewById(R.id.list_timezone);

        loadTimeZoneData();

        timeZoneItemAdapter = new TimeZoneItemAdapter(this, timeZoneBeans);
        listView.setAdapter(timeZoneItemAdapter);

        int currentIndex = findCurrentTimeZoneIndex();
        if (currentIndex >= 0) {
            timeZoneItemAdapter.setFocusedItem(currentIndex);
            listView.setSelection(currentIndex);
        }

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeZoneItemAdapter.setFocusedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 5. 点击选择时区
        listView.setOnItemClickListener((parent, view, position, id) -> {
            TimeZoneBean bean = timeZoneBeans.get(position);
            Log.i(TAG, "onItemClick: 选中 " + bean.getTimeZoneId() + " / " + bean.getTimeZoneName());

            // 切换系统时区
            setTimeZone(bean.getTimeZoneId());

            // 更新选中状态（单选）
            for (TimeZoneBean b : timeZoneBeans) {
                b.setSelect(b.getTimeZoneId().equals(bean.getTimeZoneId()));
            }
            timeZoneItemAdapter.notifyDataSetChanged();

            // 回传结果给 TimeActivity
            Intent result = new Intent();
            result.putExtra(EXTRA_TIME_ZONE_ID, bean.getTimeZoneId());
            setResult(RESULT_OK, result);
            finish();
        });

        listView.requestFocus();
    }

    private void loadTimeZoneData() {
        DateTimeUtils utils = new DateTimeUtils(this);
        Map<String, String> map = utils.getTimeZoneList();

        // 当前选中的时区 ID
        String currentTzId = TimeZone.getDefault().getID();

        List<TimeZoneBean> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String tzId = entry.getKey();
            String displayName = entry.getValue();
            TimeZone tz = TimeZone.getTimeZone(tzId);
            String gmt = formatOffset(tz.getRawOffset());
            boolean isSelect = tzId.equals(currentTzId);
            list.add(new TimeZoneBean(tzId, displayName, gmt, isSelect));
        }

        // 按 GMT 偏移量从小到大排序
        Collections.sort(list, new Comparator<TimeZoneBean>() {
            @Override
            public int compare(TimeZoneBean a, TimeZoneBean b) {
                int offA = TimeZone.getTimeZone(a.getTimeZoneId()).getRawOffset();
                int offB = TimeZone.getTimeZone(b.getTimeZoneId()).getRawOffset();
                return Integer.compare(offA, offB);
            }
        });

        timeZoneBeans.clear();
        timeZoneBeans.addAll(list);
    }
    private int findCurrentTimeZoneIndex() {
        String currentTzId = TimeZone.getDefault().getID();
        for (int i = 0; i < timeZoneBeans.size(); i++) {
            if (timeZoneBeans.get(i).getTimeZoneId().equals(currentTzId)) {
                return i;
            }
        }
        return -1;
    }

    private void setTimeZone(String timeZoneId) {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarm != null) {
            alarm.setTimeZone(timeZoneId);
            Log.i(TAG, "setTimeZone: 已切换到 " + timeZoneId);
        }
    }

    private String formatOffset(int offsetMillis) {
        int hours = offsetMillis / 3600000;
        int minutes = (offsetMillis % 3600000) / 60000;
        return String.format("GMT%+03d:%02d", hours, minutes);
    }
    class TimeZoneItemAdapter extends ArrayAdapter<TimeZoneBean> {
        private LayoutInflater inflater;

        boolean isSelected;

        private int focusedItem = 0;
        public TimeZoneItemAdapter(Context context, List<TimeZoneBean> timeZoneBeans){
            super(context,0,timeZoneBeans);
            inflater = LayoutInflater.from(context);
        }

        public void setFocusedItem(int position){
            focusedItem = position;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = inflater.inflate(R.layout.item_timezone, parent, false);
            }
            TextView tvTimeName = itemView.findViewById(R.id.tv_timename);
            TextView tvTimeGMT = itemView.findViewById(R.id.tv_gmt);
            ImageView ivCheck = itemView.findViewById(R.id.iv_check);

            TimeZoneBean timeZoneBean = getItem(position);
            if (timeZoneBean != null){
                tvTimeName.setText(timeZoneBean.getTimeZoneName());
                tvTimeGMT.setText(timeZoneBean.getGmtOffset());

                if (timeZoneBean.isSelect()) {
                    ivCheck.setVisibility(View.VISIBLE);
                } else {
                    ivCheck.setVisibility(View.INVISIBLE);
                }
            }

            if (position == focusedItem){
                itemView.setBackgroundResource(R.color.bg_focus);
                tvTimeName.setTextColor(ContextCompat.getColor(context,R.color.tv_focus));
                tvTimeGMT.setTextColor(ContextCompat.getColor(context,R.color.tv_focus));
                if (timeZoneBean.isSelect()){
                    ivCheck.setImageResource(R.drawable.ic_check_focus);
                }else {
                    ivCheck.setImageResource(R.drawable.unselected);
                }
            }else {
                itemView.setBackgroundResource(R.color.bg_normal);
                tvTimeName.setTextColor(ContextCompat.getColor(context,R.color.tv_normal));
                tvTimeGMT.setTextColor(ContextCompat.getColor(context,R.color.tv_normal));
                if (timeZoneBean.isSelect()){
                    ivCheck.setImageResource(R.drawable.ic_check_normal);
                }else {
                    ivCheck.setImageResource(R.drawable.unselected);
                }
            }
            return itemView;
        }
    }
}