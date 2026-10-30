package com.twd.gamesetting.bean;

public class SettingItem {
    public static final int TYPE_NORMAL = 0;   // 普通项：标题 + 箭头，点击进子页面
    public static final int TYPE_BRIGHT = 1;   // 亮度项：标题 + 左箭头 + 副标题 + 右箭头，左右键调亮度
    public static final int TYPE_SOUND  = 2;   // 声音项：标题 + 副标题（开启/关闭），OK 切换

    private int type;
    private int titleResId;      // 主标题字符串资源
    private String subTitle;     // 副标题当前文本（动态）
    private boolean visible;     // 是否显示
    private int actionId;        // 点击后执行的动作标识（用于 onClick 分发）

    public SettingItem(int type, int titleResId, int actionId) {
        this.type = type;
        this.titleResId = titleResId;
        this.actionId = actionId;
        this.visible = true;
        this.subTitle = "";
    }

    public int getType() { return type; }
    public int getTitleResId() { return titleResId; }
    public String getSubTitle() { return subTitle; }
    public void setSubTitle(String subTitle) { this.subTitle = subTitle; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public int getActionId() { return actionId; }
}
