package os.dtakac.feritraspored.settings.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.settings.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbarSettings)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_settings_settingsfragment, new SettingsFragment())
                .commit();
        initToolbar();
    }

    private void initToolbar(){
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}
