package os.dtakac.feritraspored.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefsUtil {

    public static void save(Context context, String key, boolean value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean get(Context context, String key, boolean defaultValue){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try{
            return sharedPrefs.getBoolean(key, defaultValue);
        } catch(Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }
}
