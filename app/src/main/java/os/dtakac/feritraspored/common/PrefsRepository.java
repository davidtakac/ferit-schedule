package os.dtakac.feritraspored.common;

import android.content.SharedPreferences;

public class PrefsRepository {

    private SharedPreferences prefs;

    public PrefsRepository(SharedPreferences prefs){
        this.prefs = prefs;
    }

    public void add(String key, String value) {
        SharedPreferences.Editor e = prefs.edit();
        e.putString(key,value);
        e.apply();
    }

    public void add(String key, int value) {
        SharedPreferences.Editor e = prefs.edit();
        e.putInt(key,value);
        e.apply();
    }

    public void add(String key, boolean value) {
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(key,value);
        e.apply();
    }

    public String get(String key, String defaultValue) {
        try {
            return prefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public int get(String key, int defaultValue) {
        try {
            return prefs.getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public boolean get(String key, boolean defaultValue) {
        try {
            return prefs.getBoolean(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
