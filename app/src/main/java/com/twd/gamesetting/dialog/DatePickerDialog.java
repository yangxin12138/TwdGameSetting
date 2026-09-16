package com.twd.gamesetting.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.twd.gamesetting.R;
import com.twd.gamesetting.interfaces.DateSelectedInterface;
import com.twd.gamesetting.utils.DateTimeUtils;
import com.twd.gamesetting.time.TimeActivity;

import java.util.Calendar;
import java.util.Locale;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 15:04 2024/6/21
 */
public class DatePickerDialog extends Dialog implements View.OnClickListener, View.OnFocusChangeListener{
    String selectDate;
    private DatePicker datePicker;
    private Context mContext;
    private DateTimeUtils utils;
    private final static String TAG = "DatePickerDialog";
    private DateSelectedInterface dateSelectedInterface;
    private String[] chineseMonths = {"一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "十二月"};
    private String[] englishMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private String[] currentMonthDisplayValues; // 当前语言对应的月份数组

    public DatePickerDialog(@NonNull Context context, DateSelectedInterface dateSelectedInterface) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        this.dateSelectedInterface = dateSelectedInterface;
        initMonthDisplayValues();
    }

    private void initMonthDisplayValues() {
        Locale currentLocale = mContext.getResources().getConfiguration().locale;
        // 判断系统语言（兼容不同Locale写法）
        if (Locale.CHINESE.getLanguage().equals(currentLocale.getLanguage())) {
            currentMonthDisplayValues = chineseMonths;
        } else {
            currentMonthDisplayValues = englishMonths;
        }
        Log.i(TAG, "initMonthDisplayValues: 当前语言=" + currentLocale.getLanguage()
                + " | 月份显示数组=" + (currentMonthDisplayValues == chineseMonths ? "中文" : "英文"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_picker);
        if (getWindow() != null) {getWindow().setLayout((int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.5), ViewGroup.LayoutParams.WRAP_CONTENT);}
        datePicker = findViewById(R.id.datePicker);
        utils = new DateTimeUtils(mContext);
        datePicker.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        datePicker.setFocusable(false); // 父控件不抢占焦点
        // 设置最早可选日期为2010年1月1日
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.set(2010,Calendar.JANUARY,1);
        long minDate = calendarMin.getTimeInMillis();
        datePicker.setMinDate(minDate);

        //设置最晚可选日期为2070年12月31日
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.set(2070,Calendar.DECEMBER,31);
        long maxDate = calendarMax.getTimeInMillis();
        datePicker.setMaxDate(maxDate);

        // 获取DatePicker的年、月、日NumberPicker（核心：提前获取并固定月份显示）
        NumberPicker yearPicker = null;
        NumberPicker monthPicker = null;
        NumberPicker dayPicker = null;
        try {
            // 兼容不同Android版本的DatePicker布局
            ViewGroup firstChild = (ViewGroup) datePicker.getChildAt(0);
            ViewGroup secondChild = (ViewGroup) firstChild.getChildAt(0);
            yearPicker = (NumberPicker) secondChild.getChildAt(0);
            monthPicker = (NumberPicker) secondChild.getChildAt(1);
            dayPicker = (NumberPicker) secondChild.getChildAt(2);

            // 核心修复1：重新排序年/月/日（适配美国格式：月/日/年）
            Locale currentLocale = mContext.getResources().getConfiguration().locale;
            if (Locale.US.getLanguage().equals(currentLocale.getLanguage())) {
                // 美国格式：月/日/年 → 调整picker顺序
                monthPicker = (NumberPicker) secondChild.getChildAt(0);
                dayPicker = (NumberPicker) secondChild.getChildAt(1);
                yearPicker = (NumberPicker) secondChild.getChildAt(2);
                Log.i(TAG, "onCreate: 美国格式 → 月/日/年");
            } else {
                // 其他格式：年/月/日
                Log.i(TAG, "onCreate: 中文格式 → 年/月/日");
            }

            // 核心修复2：固定月份选择器的显示值，禁用系统自动格式化
            if (monthPicker != null) {
                // 1. 先清空系统默认的显示值
                monthPicker.setDisplayedValues(null);
                // 2. 设置自定义月份数组（中文/英文）
                monthPicker.setDisplayedValues(currentMonthDisplayValues);
                // 3. 强制设置月份范围（0-11 对应1-12月）
                monthPicker.setMinValue(0);
                monthPicker.setMaxValue(11);
                // 4. 禁用系统自动包装，防止还原
                monthPicker.setWrapSelectorWheel(true);
                // 5. 强制刷新显示（核心：解决初始显示数字的问题）
                monthPicker.setValue(datePicker.getMonth()); // 设为当前月份
                monthPicker.invalidate(); // 刷新视图
                NumberPicker finalMonthPicker1 = monthPicker;
                monthPicker.post(() -> {
                    // 延迟刷新，确保生效
                    finalMonthPicker1.setDisplayedValues(currentMonthDisplayValues);
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "获取NumberPicker失败", e);
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // 0-11 对应1-12月
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 初始化DatePicker显示当前日期
        NumberPicker finalMonthPicker = monthPicker;
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int displayMonth = monthOfYear + 1;
                selectDate = String.format("%04d/%02d/%02d", year, displayMonth, dayOfMonth);
                Log.i(TAG, "onDateChanged: 日期是 = " + selectDate);

                // 防滚动后格式还原：重新设置月份显示值
                if (finalMonthPicker != null) {
                    finalMonthPicker.setDisplayedValues(currentMonthDisplayValues);
                }
            }
        });

        // 初始化默认选中日期
        selectDate = String.format("%04d/%02d/%02d", year, month + 1, day);

        // 设置点击和焦点监听（增加非空判断，避免空指针）
        if (yearPicker != null) {
            yearPicker.setOnClickListener(this);
            yearPicker.setOnFocusChangeListener(this);
        }
        if (monthPicker != null) {
            monthPicker.setOnClickListener(this);
            monthPicker.setOnFocusChangeListener(this);
        }
        if (dayPicker != null) {
            dayPicker.setOnClickListener(this);
            dayPicker.setOnFocusChangeListener(this);
        }

        // 请求焦点
        NumberPicker finalMonthPicker2 = monthPicker;
        datePicker.post(new Runnable() {
            @Override
            public void run() {
                datePicker.requestFocus();
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (dateSelectedInterface != null){
            Log.i(TAG, "onClick: selectDate is Empty:"+selectDate.isEmpty());
            dateSelectedInterface.onDateSelected(selectDate);
        }
        dismiss();
        Intent restartIntent = new Intent(mContext.getApplicationContext(), TimeActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(restartIntent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            Log.i(TAG, "onFocusChange: 日期聚焦获取焦点");
            v.setBackgroundResource(R.drawable.dialog_item_background);
        }else {
            v.setBackgroundResource(0);
        }
    }
}
