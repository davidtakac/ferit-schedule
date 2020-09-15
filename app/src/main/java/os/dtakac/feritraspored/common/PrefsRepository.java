package os.dtakac.feritraspored.common;

import android.content.SharedPreferences;
import android.content.res.Resources;

public class PrefsRepository {

    private Resources res;
    private SharedPreferences prefs;

    public PrefsRepository(SharedPreferences prefs, Resources res){
        this.prefs = prefs;
        this.res = res;
    }

    public void add(int keyId, String value) {
        SharedPreferences.Editor e = prefs.edit();
        e.putString(res.getString(keyId), value);
        e.apply();
    }

    public void add(int keyId, int value) {
        SharedPreferences.Editor e = prefs.edit();
        e.putInt(res.getString(keyId), value);
        e.apply();
    }

    public void add(int keyId, boolean value) {
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(res.getString(keyId), value);
        e.apply();
    }

    public String get(int keyId, String defaultValue) {
        try {
            return prefs.getString(res.getString(keyId), defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public int get(int keyId, int defaultValue) {
        try {
            return prefs.getInt(res.getString(keyId), defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public boolean get(int keyId, boolean defaultValue) {
        try {
            return prefs.getBoolean(res.getString(keyId), defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
