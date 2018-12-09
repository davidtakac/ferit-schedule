package os.dtakac.feritraspored.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.ui.ScheduleActivity;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.util.SharedPrefsUtil;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_settings_settingsfragment, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: {
                if(!SharedPrefsUtil.get(this, Constants.PREVIOUSLY_STARTED, false)){
                    SharedPrefsUtil.save(this, Constants.PREVIOUSLY_STARTED, true);
                    startActivity(new Intent(this, ScheduleActivity.class));
                } else {
                    Log.d(Constants.LOG_TAG, "finishing");
                    finish();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
