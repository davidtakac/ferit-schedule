package os.dtakac.feritraspored;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import os.dtakac.feritraspored.common.preferences.PreferenceRepository;
import os.dtakac.feritraspored.common.preferences.PreferenceRepositoryImpl;
import os.dtakac.feritraspored.common.resources.ResourceRepositoryImpl;

public class App extends Application {
    private PreferenceRepository prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = new PreferenceRepositoryImpl(
                new ResourceRepositoryImpl(getResources()),
                PreferenceManager.getDefaultSharedPreferences(this)
        );
        initTheme();
        migrateToCourseIdentifierPreference();
    }
    private void initTheme(){
        String theme = prefs.getTheme();
        if(theme == null){
            String defaultTheme = Integer.toString(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            prefs.setTheme(defaultTheme);
            theme = defaultTheme;
        }
        AppCompatDelegate.setDefaultNightMode(Integer.parseInt(theme));
    }

    private void migrateToCourseIdentifierPreference() {
        if(prefs.getCourseIdentifier() == null) {
            String year = prefs.getYear();
            String programme = prefs.getProgramme();
            if(year != null && programme != null) {
                String courseIdentifier = year + "-" + programme;
                prefs.setCourseIdentifier(courseIdentifier);
                prefs.delete(R.string.key_year);
                prefs.delete(R.string.key_programme);
            }
        }
    }
}
