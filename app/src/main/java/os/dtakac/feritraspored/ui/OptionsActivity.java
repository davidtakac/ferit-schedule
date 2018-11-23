package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import os.dtakac.feritraspored.App;
import os.dtakac.feritraspored.model.programmes.Programme;
import os.dtakac.feritraspored.model.programmes.Programmes;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.util.SharedPrefsUtil;
import os.dtakac.feritraspored.model.programmes.ProgrammeType;
import os.dtakac.feritraspored.model.year.Year;

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

    @BindView(R.id.et_options_labgroups)
    EditText etGroupFilter;

    private ArrayAdapter<Programme> progAdapter;

    private ArrayAdapter<Year> yearAdapter;

    private Programmes programmes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ButterKnife.bind(this);

        programmes = App.getProgrammes();

        setSpinnersEnabled(false);
        setSaveEnabled(false);
        initViewsFromPrefs();
    }

    private void initViewsFromPrefs() {
        initRadioGroup();
        initSpinners();
        initOtherOptions();
        initGroupFilter();
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

        int prevChecked = SharedPrefsUtil.get(this, Constants.CHECKED_PROGTYPE_ID_IKEY, 0);
        rgProgType.check(prevChecked != 0 ? prevChecked : R.id.rb_options_undergrad);
    }

    private void initSpinners(){
        int prevChkProgPos = SharedPrefsUtil.get(this, Constants.CHECKED_PROG_POS_KEY, -1);
        int prevYearPos = SharedPrefsUtil.get(this, Constants.CHECKED_YEAR_POS_KEY, -1);

        spnProg.setSelection(prevChkProgPos != -1 ? prevChkProgPos : 0);
        spnYear.setSelection(prevYearPos != -1 ? prevYearPos : 0);
    }

    private void initOtherOptions() {
        swSkipSaturday.setChecked(SharedPrefsUtil.get(this, Constants.SKIP_SATURDAY_KEY, false));
        swNextDayAt8pm.setChecked(SharedPrefsUtil.get(this, Constants.NEXTDAY_AFTER_8PM_KEY, false));
    }

    private void initGroupFilter(){
        etGroupFilter.setText(SharedPrefsUtil.get(this, Constants.GROUP_FILTER_KEY, ""));
    }

    @OnClick(R.id.btn_options_save)
    void saveOptions(){
        saveOptionsToPrefs();
        startScheduleActivity();
    }

    private void saveOptionsToPrefs() {
        //save programme and year
        SharedPrefsUtil.save(this, Constants.PROGRAMME_KEY, progAdapter.getItem(spnProg.getSelectedItemPosition()).getId());
        SharedPrefsUtil.save(this, Constants.YEAR_KEY, yearAdapter.getItem(spnYear.getSelectedItemPosition()).getId());

        //save other options
        SharedPrefsUtil.save(this, Constants.SKIP_SATURDAY_KEY, swSkipSaturday.isChecked());
        SharedPrefsUtil.save(this, Constants.NEXTDAY_AFTER_8PM_KEY, swNextDayAt8pm.isChecked());
        SharedPrefsUtil.save(this, Constants.GROUP_FILTER_KEY, etGroupFilter.getText().toString());

        //save options for pre-selection
        SharedPrefsUtil.save(this, Constants.CHECKED_PROGTYPE_ID_IKEY, rgProgType.getCheckedRadioButtonId());
        SharedPrefsUtil.save(this, Constants.CHECKED_PROG_POS_KEY, spnProg.getSelectedItemPosition());
        SharedPrefsUtil.save(this, Constants.CHECKED_YEAR_POS_KEY, spnYear.getSelectedItemPosition());
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
        progAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                programmes.getProgrammes(type)
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

    private void setSaveEnabled(boolean isEnabled) {
        btnSave.setEnabled(isEnabled);
    }

    private void startScheduleActivity() {
        startActivity(new Intent(this, ScheduleActivity.class));
    }
}
