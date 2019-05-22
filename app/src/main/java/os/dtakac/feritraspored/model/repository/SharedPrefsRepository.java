package os.dtakac.feritraspored.model.repository;

import android.content.SharedPreferences;

public class SharedPrefsRepository implements IRepository {

    private SharedPreferences prefs;

    public SharedPrefsRepository(SharedPreferences prefs){
        this.prefs = prefs;
    }

    @Override
    public void add(String key, String value) {
        SharedPreferences.Editor e = prefs.edit();
        e.putString(key,value);
        e.apply();
    }

    @Override
    public void add(String key, int value) {
        SharedPreferences.Editor e = prefs.edit();
        e.putInt(key,value);
        e.apply();
    }

    @Override
    public void add(String key, boolean value) {
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(key,value);
        e.apply();
    }

    @Override
    public String get(String key, String defaultValue) {
        try {
            return prefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @Override
    public int get(String key, int defaultValue) {
        try {
            return prefs.getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @Override
    public boolean get(String key, boolean defaultValue) {
        try {
            return prefs.getBoolean(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
