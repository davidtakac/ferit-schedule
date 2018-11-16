package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import butterknife.BindView;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.util.SharedPrefsUtil;

// TODO: 14-Nov-18 make startup activity that makes user choose year and programme
public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.wv_schedule)
    WebView wvSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //if the app was started for the first time, let the user pick his programme and year
        if(!SharedPrefsUtil.get(this, Constants.PREVIOUSLY_STARTED, false)){
            SharedPrefsUtil.save(this, Constants.PREVIOUSLY_STARTED, true);
            startActivity(new Intent(this, ProgrammeYearPickerActivity.class));
        } else {
            startActivity(new Intent(this, ScheduleActivity.class));
        }
        finish();
    }
}
