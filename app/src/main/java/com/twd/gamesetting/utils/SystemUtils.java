package com.twd.gamesetting.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SystemUtils {
    private static final String TAG = "SystemPropertiesUtils";
    private static final String CLASS_NAME = "android.os.SystemProperties";
    public static String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try{
            Class<?> c = Class.forName(CLASS_NAME);
            Method get = c.getMethod("get",String.class, String.class);
            value = (String)(get.invoke(c,key,defaultValue));
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

    public static int readSysNode(String path, int defaultValue) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
        return defaultValue;
    }

    public static boolean writeSysNode(String path, int value) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(path);
            writer.write(String.valueOf(value));
            writer.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException ignored) {}
            }
        }
    }
}
