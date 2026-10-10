package com.twd.gamesetting.time;

public class TimeZoneBean {
    private final String timeZoneId;    // 例如 "Asia/Shanghai"，用于 setTimeZone
    private final String timeZoneName;  // 显示名，例如 "中国标准时间 (北京)"
    private final String gmtOffset;     // 显示用，例如 "GMT+08:00"
    private boolean isSelect;           // 是否是当前选中时区

    public TimeZoneBean(String timeZoneId, String timeZoneName, String gmtOffset, boolean isSelect) {
        this.timeZoneId = timeZoneId;
        this.timeZoneName = timeZoneName;
        this.gmtOffset = gmtOffset;
        this.isSelect = isSelect;
    }

    public String getTimeZoneId() { return timeZoneId; }
    public String getTimeZoneName() { return timeZoneName; }
    public String getGmtOffset() { return gmtOffset; }
    public boolean isSelect() { return isSelect; }
    public void setSelect(boolean select) { isSelect = select; }
}
