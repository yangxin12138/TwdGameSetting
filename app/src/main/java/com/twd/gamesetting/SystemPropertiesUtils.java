package com.twd.gamesetting;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class SystemPropertiesUtils {
    private static final String CLASS_NAME = "android.os.SystemProperties";

    private static final String INI_LAUNCHER_KEY = "LAUNCHER_PROJECTOR";


    public static <T> T getProperty(String key, T defaultValue){

        T value = defaultValue;
        try{
            // 将 defaultValue 转换为 String 类型
            String defaultStringValue = defaultValue != null ? defaultValue.toString() : null;
            Class<?> c = Class.forName(CLASS_NAME);
            Method get = c.getMethod("get",String.class, String.class);
            String result = (String) get.invoke(null, key, defaultStringValue);

            if (defaultValue instanceof Boolean) {
                // 如果 defaultValue 是 Boolean 类型，将结果转换为 Boolean
                value = (T) Boolean.valueOf(result);
            } else {
                value = (T) result;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return value;
        }
    }

    public static void setProperty(String key,String value){
        try{
            Class<?> c = Class.forName(CLASS_NAME);
            Method set = c.getMethod("set",String.class,String.class);
            set.invoke(c,key,value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getPropertyColor(String key,String defaultValue){
        String value = defaultValue;
        try{
            Class<?> c = Class.forName(CLASS_NAME);
            Method get = c.getMethod("get",String.class,String.class);
            value = (String) (get.invoke(c,key,defaultValue));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return value;
        }
    }

    //TODO 从 /etc/settings.ini 读取启动入口配置
    public static boolean whichLauncherActivity(){
        String iniValue = readSystemProp(INI_LAUNCHER_KEY);
        Log.i("yangxin", "whichLauncherActivity: iniValue = " + Boolean.parseBoolean(iniValue));
        return Boolean.parseBoolean(iniValue);
    }

    public static String readSystemProp(String search_line) {
        String line = "";
        try {
            File file = new File("/system/etc/settings.ini");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((line = reader.readLine()) != null) {
                if (line.contains(search_line)) {
                    // 这里可以进一步解析line来获取STORAGE_SIMPLE_SYSDATA的值
                    String value = line.split("=")[1].trim(); // 获取等号后面的值
                    Log.i("yangxin", "readSystemProp: value = "+ value);
                    reader.close();
                    fis.close();
                    return value;
                }
            }
            reader.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "false";
    }

    public static String readFile(String path){
        File file = new File(path);
        if (!file.exists()) {
            Log.w("systemUtils", path + " not exist");
            return "";
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            return line;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return "";
    }
}
