package net.m_schwarz.journe;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by michael on 05.11.16.
 */

public class BitmapCache {
    public static Map<String,Bitmap> map = new HashMap<>();
    public static Set<String> temptative = new HashSet<>();

    public static void putTemptative(String str){
        temptative.add(str);
    }

    public static boolean inTemptative(String str){
        return temptative.contains(str);
    }

    public static void put(String str,Bitmap bmp){
        map.put(str,bmp);
    }

    public static boolean contains(String str){
        return map.containsKey(str);
    }

    public static Bitmap get(String str){
        return map.get(str);
    }
}
