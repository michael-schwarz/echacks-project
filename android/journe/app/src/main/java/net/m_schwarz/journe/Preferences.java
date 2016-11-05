package net.m_schwarz.journe;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by michael on 05.11.16.
 */
public class Preferences {
    public static final String PREFS_NAME = "JournePrefs";
    SharedPreferences settings;

    public Preferences(Context context){
        settings = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public boolean isLoggedIn(){
        return settings.getBoolean("loggedIn",false);
    }

    public void setUserId(int id){
        SharedPreferences.Editor editor =settings.edit();
        editor.putBoolean("loggedIn",true);
        editor.putInt("userId",id);
        editor.commit();
    }

    public int getUserId() throws IllegalStateException{
        if(!settings.getBoolean("loggedIn",false)){
            throw new IllegalStateException();
        }

        return settings.getInt("userId",0);
    }

    public void logOut(){
        SharedPreferences.Editor editor =settings.edit();
        editor.putBoolean("loggedIn",false);
        editor.commit();
    }
}
