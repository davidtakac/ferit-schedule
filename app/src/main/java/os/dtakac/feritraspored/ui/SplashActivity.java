package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import butterknife.BindView;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.util.SharedPrefsUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        if(!SharedPrefsUtil.get(this, Constants.PREVIOUSLY_STARTED, false)){
            //if the app was started for the first time, let the user pick his programme and year

            SharedPrefsUtil.save(this, Constants.PREVIOUSLY_STARTED, true);
            startActivity(new Intent(this, OptionsActivity.class));
        } else {
            startActivity(new Intent(this, ScheduleActivity.class));
        }
        finish();
    }
}
