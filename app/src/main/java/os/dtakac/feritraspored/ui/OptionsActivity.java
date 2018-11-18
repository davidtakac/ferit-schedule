package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.util.SharedPrefsUtil;
import os.dtakac.feritraspored.model.programmes.Differential;
import os.dtakac.feritraspored.model.programmes.Graduate;
import os.dtakac.feritraspored.model.programmes.Professional;
import os.dtakac.feritraspored.model.programmes.Programme;
import os.dtakac.feritraspored.model.programmes.ProgrammeType;
import os.dtakac.feritraspored.model.programmes.Undergrad;
import os.dtakac.feritraspored.model.programmes.Year;

// TODO: 16-Nov-18 make spinners prettier
public class OptionsActivity extends AppCompatActivity {

    @BindView(R.id.rg_options_pickprogtype)
    RadioGroup rgProgType;

    @BindView(R.id.spn_options_pickprogramme)
    Spinner spnProg;

    @BindView(R.id.spn_options_pickyear)
    Spinner spnYear;

    @BindView(R.id.btn_options_save)
    Button btnSave;

    @BindView(R.id.sw_options_skipsat)
    Switch swSkipSaturday;

    @BindView(R.id.sw_options_nextday8pm)
    Switch swNextDayAt8pm;

    private ArrayAdapter<Programme> progAdapter;

    private ArrayAdapter<Year> yearAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ButterKnife.bind(this);

        setSpinnersEnabled(false);
        setSaveEnabled(false);
        initRadioGroup();
    }

    @OnClick(R.id.btn_options_save)
    void saveOptions(){
        //save programme and year
        SharedPrefsUtil.save(this, Constants.PROGRAMME_KEY, progAdapter.getItem(spnProg.getSelectedItemPosition()).getId());
        SharedPrefsUtil.save(this, Constants.YEAR_KEY, yearAdapter.getItem(spnYear.getSelectedItemPosition()).getId());

        //save other options
        SharedPrefsUtil.save(this, Constants.SKIP_SATURDAY_KEY, swSkipSaturday.isChecked());
        SharedPrefsUtil.save(this, Constants.NEXTDAY_AT_8PM_KEY, swNextDayAt8pm.isChecked());

        startScheduleActivity();
    }

    private void setSpinnerDataBasedOnId(int checkedId) {
        ProgrammeType type = ProgrammeType.UNDERGRAD;

        switch(checkedId){
            case R.id.rb_options_grad: type = ProgrammeType.GRAD; break;
            case R.id.rb_options_prof: type = ProgrammeType.PROF; break;
            case R.id.rb_options_diff: type = ProgrammeType.DIFF; break;
            default: break;
        }

        setProgSpinnerData(type);
        setYearSpinnerData(type);
    }

    private void setProgSpinnerData(ProgrammeType type) {
        Programme[] dataToDisplay = Undergrad.values();

        switch (type){
            case GRAD: dataToDisplay = Graduate.values(); break;
            case PROF: dataToDisplay = Professional.values(); break;
            case DIFF: dataToDisplay = Differential.values(); break;
            default: break;
        }

        progAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dataToDisplay
        );

        spnProg.setAdapter(progAdapter);
    }

    private void setYearSpinnerData(ProgrammeType type) {
        yearAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                type.getYears()
        );

        spnYear.setAdapter(yearAdapter);
    }

    private void setSpinnersEnabled(boolean isEnabled) {
        spnProg.setEnabled(isEnabled);
        spnYear.setEnabled(isEnabled);
    }

    private void startScheduleActivity() {
        startActivity(new Intent(this, SplashActivity.class));
    }

    private void setSaveEnabled(boolean isEnabled) {
        btnSave.setEnabled(isEnabled);
    }

    private void initRadioGroup(){
        rgProgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setSpinnersEnabled(true);
                setSaveEnabled(true);
                setSpinnerDataBasedOnId(checkedId);
            }
        });

        rgProgType.check(R.id.rb_options_undergrad);
    }
}
