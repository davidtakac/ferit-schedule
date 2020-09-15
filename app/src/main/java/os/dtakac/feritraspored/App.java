package os.dtakac.feritraspored;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initTheme();
    }
    private void initTheme(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = prefs.getString(getString(R.string.key_theme), null);
        if(theme == null){
            String defaultTheme = Integer.toString(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            prefs.edit().putString(getString(R.string.key_theme), defaultTheme).apply();
            theme = defaultTheme;
        }
        AppCompatDelegate.setDefaultNightMode(Integer.parseInt(theme));
    }
}
