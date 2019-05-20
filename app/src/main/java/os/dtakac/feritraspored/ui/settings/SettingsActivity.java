package os.dtakac.feritraspored.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import os.dtakac.feritraspored.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_settings_settingsfragment, new SettingsFragment())
                .commit();
    }

    private void setTheme(){
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkTheme = s.getBoolean(getString(R.string.prefkey_darktheme), false);

        setTheme(darkTheme ? R.style.DarkTheme : R.style.LightTheme);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: {
                finish();
                return true;
            }
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }
}
