package com.twd.gamesetting.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;

import com.twd.gamesetting.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 14:59 2024/6/21
 */
public class DateTimeUtils {
    private Context mContext;

    public DateTimeUtils(Context mContext) {
        this.mContext = mContext;
    }

    public static String getTimeFormat(Context context){
        //获取当前local
        Locale locale = Locale.getDefault();

        //检查是否是24小时制
        if (DateFormat.is24HourFormat(context)){
            return "HH:mm";
        } else {
            return "hh:mm a";
        }
    }

    public boolean isNetworkTimeEnabled(){
        //读取系统设置
        return Settings.Global.getInt(mContext.getContentResolver(),Settings.Global.AUTO_TIME,0) != 0;
    }

    public boolean is24HoursEnabled(){
        return DateFormat.is24HourFormat(mContext);
    }
    public boolean isAutoTimeZone(){
        return Objects.equals(Settings.Global.getString(mContext.getContentResolver(), Settings.Global.AUTO_TIME_ZONE), "1");
    }

    public Map<String, String> getTimeZoneList(){
        Map<String, String> timeZoneDisplayNames = new HashMap<>();
        timeZoneDisplayNames.put("Pacific/Majuro",mContext.getString(R.string.time_zone_Majuro)); //马朱罗
        timeZoneDisplayNames.put("Pacific/Midway",mContext.getString(R.string.time_zone_Midway));  //中途岛
        timeZoneDisplayNames.put("Pacific/Honolulu",mContext.getString(R.string.time_zone_Honolulu)); //檀香山
        timeZoneDisplayNames.put("America/Anchorage",mContext.getString(R.string.time_zone_Anchorage));  //安克雷奇
        timeZoneDisplayNames.put("America/Los_Angeles",mContext.getString(R.string.time_zone_Los_Angeles));  //美国太平洋时间 (洛杉矶)
        timeZoneDisplayNames.put("America/Tijuana",mContext.getString(R.string.time_zone_Tijuana)); //美国太平洋时间 (提华纳)
        timeZoneDisplayNames.put("America/Phoenix",mContext.getString(R.string.time_zone_Phoenix)); //美国山区时间 (凤凰城)
        timeZoneDisplayNames.put("America/Chihuahua",mContext.getString(R.string.time_zone_Chihuahua)); //奇瓦瓦
        timeZoneDisplayNames.put("America/Denver",mContext.getString(R.string.time_zone_Denver)); //美国山区时间 (丹佛)
        timeZoneDisplayNames.put("America/Costa_Rica",mContext.getString(R.string.time_zone_Costa_Rica)); //美国中部时间 (哥斯达黎加)
        timeZoneDisplayNames.put("America/Chicago",mContext.getString(R.string.time_zone_Chicago)); //美国中部时间 (芝加哥)
        timeZoneDisplayNames.put("America/Mexico_City",mContext.getString(R.string.time_zone_Mexico_City)); //美国中部时间 (墨西哥城)
        timeZoneDisplayNames.put("America/Regina",mContext.getString(R.string.time_zone_Regina)); //美国中部时间 (里贾纳)
        timeZoneDisplayNames.put("America/Bogota",mContext.getString(R.string.time_zone_Bogota)); //哥伦比亚时间 (波哥大)
        timeZoneDisplayNames.put("America/New_York",mContext.getString(R.string.time_zone_New_York)); //美国东部时间 (纽约)
        timeZoneDisplayNames.put("America/Caracas",mContext.getString(R.string.time_zone_Caracas)); //委内瑞拉时间 (加拉加斯)
        timeZoneDisplayNames.put("America/Barbados",mContext.getString(R.string.time_zone_Barbados)); //大西洋时间 (巴巴多斯)
        timeZoneDisplayNames.put("America/Manaus",mContext.getString(R.string.time_zone_Manaus)); //亚马逊标准时间 (马瑙斯)
        timeZoneDisplayNames.put("America/Santiago",mContext.getString(R.string.time_zone_Santiago)); //圣地亚哥
        timeZoneDisplayNames.put("America/St_Johns",mContext.getString(R.string.time_zone_St_Johns)); //纽芬兰时间 (圣约翰)
        timeZoneDisplayNames.put("America/Argentina/Buenos_Aires",mContext.getString(R.string.time_zone_Buenos_Aires)); //布宜诺斯艾利斯
        timeZoneDisplayNames.put("America/Godthab",mContext.getString(R.string.time_zone_Godthab)); //戈特霍布
        timeZoneDisplayNames.put("America/Montevideo",mContext.getString(R.string.time_zone_Montevideo)); //乌拉圭时间 (蒙得维的亚)
        timeZoneDisplayNames.put("Atlantic/South_Georgia",mContext.getString(R.string.time_zone_South_Georgia)); //南乔治亚
        timeZoneDisplayNames.put("Atlantic/Azores",mContext.getString(R.string.time_zone_Azores)); //亚述尔群岛
        timeZoneDisplayNames.put("Atlantic/Cape_Verde",mContext.getString(R.string.time_zone_Cape_Verde)); //佛得角
        timeZoneDisplayNames.put("Africa/Casablanca",mContext.getString(R.string.time_zone_Casablanca)); //卡萨布兰卡
        timeZoneDisplayNames.put("Europe/London",mContext.getString(R.string.time_zone_London)); //格林尼治标准时间 (伦敦)
        timeZoneDisplayNames.put("Europe/Amsterdam",mContext.getString(R.string.time_zone_Amsterdam)); //中欧标准时间 (阿姆斯特丹)
        timeZoneDisplayNames.put("Europe/Belgrade",mContext.getString(R.string.time_zone_Belgrade)); //中欧标准时间 (贝尔格莱德)
        timeZoneDisplayNames.put("Europe/Brussels",mContext.getString(R.string.time_zone_Brussels)); //中欧标准时间 (布鲁塞尔)
        timeZoneDisplayNames.put("Europe/Sarajevo",mContext.getString(R.string.time_zone_Sarajevo)); //中欧标准时间 (萨拉热窝)
        timeZoneDisplayNames.put("Africa/Windhoek",mContext.getString(R.string.time_zone_Windhoek)); //温得和克
        timeZoneDisplayNames.put("Africa/Brazzaville",mContext.getString(R.string.time_zone_Brazzaville)); //西部非洲标准时间 (布拉扎维)
        timeZoneDisplayNames.put("Asia/Amman",mContext.getString(R.string.time_zone_Amman)); //东欧标准时间 (安曼)
        timeZoneDisplayNames.put("Europe/Athens",mContext.getString(R.string.time_zone_Athens)); //东欧标准时间 (雅典)
        timeZoneDisplayNames.put("Asia/Beirut",mContext.getString(R.string.time_zone_Beirut)); //东欧标准时间 (贝鲁特)
        timeZoneDisplayNames.put("Africa/Cairo",mContext.getString(R.string.time_zone_Cairo)); //东欧标准时间 (开罗)
        timeZoneDisplayNames.put("Europe/Helsinki",mContext.getString(R.string.time_zone_Helsinki)); //东欧标准时间 (赫尔辛基)
        timeZoneDisplayNames.put("Asia/Jerusalem",mContext.getString(R.string.time_zone_Jerusalem)); //以色列时间 (耶路撒冷)
        timeZoneDisplayNames.put("Europe/Minsk",mContext.getString(R.string.time_zone_Minsk)); //明斯克
        timeZoneDisplayNames.put("Africa/Harare",mContext.getString(R.string.time_zone_Harare)); //中部非洲标准时间 (哈拉雷)
        timeZoneDisplayNames.put("Asia/Baghdad",mContext.getString(R.string.time_zone_Baghdad)); //巴格达
        timeZoneDisplayNames.put("Europe/Moscow",mContext.getString(R.string.time_zone_Moscow)); //莫斯科
        timeZoneDisplayNames.put("Asia/Kuwait",mContext.getString(R.string.time_zone_Kuwait)); //科威特
        timeZoneDisplayNames.put("Africa/Nairobi",mContext.getString(R.string.time_zone_Nairobi)); //东部非洲标准时间 (内罗毕)
        timeZoneDisplayNames.put("Asia/Tehran",mContext.getString(R.string.time_zone_Tehran)); //伊朗标准时间 (德黑兰)
        timeZoneDisplayNames.put("Asia/Baku",mContext.getString(R.string.time_zone_Baku)); //巴库
        timeZoneDisplayNames.put("Asia/Tbilisi",mContext.getString(R.string.time_zone_Tbilisi)); //第比利斯
        timeZoneDisplayNames.put("Asia/Yerevan",mContext.getString(R.string.time_zone_Yerevan)); //埃里温
        timeZoneDisplayNames.put("Asia/Dubai",mContext.getString(R.string.time_zone_Dubai)); //迪拜
        timeZoneDisplayNames.put("Asia/Kabul",mContext.getString(R.string.time_zone_Kabul)); //阿富汗时间 (喀布尔)
        timeZoneDisplayNames.put("Asia/Karachi",mContext.getString(R.string.time_zone_Karachi)); //卡拉奇
        timeZoneDisplayNames.put("Asia/Oral",mContext.getString(R.string.time_zone_Oral)); //乌拉尔
        timeZoneDisplayNames.put("Asia/Yekaterinburg",mContext.getString(R.string.time_zone_Yekaterinburg)); //叶卡捷林堡
        timeZoneDisplayNames.put("Asia/Calcutta",mContext.getString(R.string.time_zone_Calcutta)); //加尔各答
        timeZoneDisplayNames.put("Asia/Colombo",mContext.getString(R.string.time_zone_Colombo)); //科伦坡
        timeZoneDisplayNames.put("Asia/Katmandu",mContext.getString(R.string.time_zone_Katmandu)); //尼泊尔时间 (加德满都)
        timeZoneDisplayNames.put("Asia/Almaty",mContext.getString(R.string.time_zone_Almaty)); //阿拉木图
        timeZoneDisplayNames.put("Asia/Rangoon",mContext.getString(R.string.time_zone_Rangoon)); //缅甸时间 (仰光)
        timeZoneDisplayNames.put("Asia/Krasnoyarsk",mContext.getString(R.string.time_zone_Krasnoyarsk)); //克拉斯诺亚尔斯克
        timeZoneDisplayNames.put("Asia/Bangkok",mContext.getString(R.string.time_zone_Bangkok)); //曼谷
        timeZoneDisplayNames.put("Asia/Shanghai",mContext.getString(R.string.time_zone_Shanghai)); //中国标准时间 (北京)
        timeZoneDisplayNames.put("Asia/Hong_Kong",mContext.getString(R.string.time_zone_Hong_Kong)); //香港时间 (香港)
        timeZoneDisplayNames.put("Asia/Irkutsk",mContext.getString(R.string.time_zone_Irkutsk)); //伊尔库茨克时间 (伊尔库茨克)
        timeZoneDisplayNames.put("Asia/Kuala_Lumpur",mContext.getString(R.string.time_zone_Kuala_Lumpur)); //吉隆坡
        timeZoneDisplayNames.put("Australia/Perth",mContext.getString(R.string.time_zone_Perth)); //佩思
        timeZoneDisplayNames.put("Asia/Taipei",mContext.getString(R.string.time_zone_Taipei)); //台北时间 (台北)
        timeZoneDisplayNames.put("Asia/Seoul",mContext.getString(R.string.time_zone_Seoul)); //首尔
        timeZoneDisplayNames.put("Asia/Tokyo",mContext.getString(R.string.time_zone_Tokyo)); //日本时间 (东京)
        timeZoneDisplayNames.put("Asia/Yakutsk",mContext.getString(R.string.time_zone_Yakutsk)); //雅库茨克时间 (雅库茨克)
        timeZoneDisplayNames.put("Australia/Adelaide",mContext.getString(R.string.time_zone_Adelaide)); //阿德莱德
        timeZoneDisplayNames.put("Australia/Darwin",mContext.getString(R.string.time_zone_Darwin)); //达尔文
        timeZoneDisplayNames.put("Australia/Brisbane",mContext.getString(R.string.time_zone_Brisbane)); //布里斯班
        timeZoneDisplayNames.put("Australia/Hobart",mContext.getString(R.string.time_zone_Hobart)); //霍巴特
        timeZoneDisplayNames.put("Australia/Sydney",mContext.getString(R.string.time_zone_Sydney)); //悉尼
        timeZoneDisplayNames.put("Asia/Vladivostok",mContext.getString(R.string.time_zone_Vladivostok)); //海参崴时间 (符拉迪沃斯托克)
        timeZoneDisplayNames.put("Pacific/Guam",mContext.getString(R.string.time_zone_Guam)); //关岛
        timeZoneDisplayNames.put("Asia/Magadan",mContext.getString(R.string.time_zone_Magadan)); //马加丹时间 (马加丹)
        timeZoneDisplayNames.put("Pacific/Auckland",mContext.getString(R.string.time_zone_Auckland)); //奥克兰
        timeZoneDisplayNames.put("Pacific/Fiji",mContext.getString(R.string.time_zone_Fiji)); //斐济
        timeZoneDisplayNames.put("Pacific/Tongatapu",mContext.getString(R.string.time_zone_Tongatapu)); //东加塔布
        timeZoneDisplayNames.put("Europe/Madrid", mContext.getString(R.string.time_zone_Madrid));//西班牙马德里
        return timeZoneDisplayNames;
    }

    public void hideSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
