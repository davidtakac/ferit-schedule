package os.dtakac.feritraspored;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import os.dtakac.feritraspored.common.PrefsRepository;

public class App extends Application {
    private PrefsRepository prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = new PrefsRepository(
                PreferenceManager.getDefaultSharedPreferences(this),
                getResources()
        );
        initTheme();
        migrateToCourseIdentifierPreference();
    }
    private void initTheme(){
        String theme = prefs.get(R.string.key_theme, null);
        if(theme == null){
            String defaultTheme = Integer.toString(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            prefs.add(R.string.key_theme, defaultTheme);
            theme = defaultTheme;
        }
        AppCompatDelegate.setDefaultNightMode(Integer.parseInt(theme));
    }

    private void migrateToCourseIdentifierPreference() {
        if(prefs.get(R.string.key_course_identifier, null) == null) {
            String year = prefs.get(R.string.key_year, null);
            String programme = prefs.get(R.string.key_programme, null);
            if(year != null && programme != null) {
                String courseIdentifier = year + "-" + programme;
                prefs.add(R.string.key_course_identifier, courseIdentifier);
                prefs.delete(R.string.key_year);
                prefs.delete(R.string.key_programme);
            }
        }
    }
}
