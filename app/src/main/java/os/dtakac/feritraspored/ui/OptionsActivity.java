package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @BindView(R.id.sw_options_skipsat)
    Switch swSkipSaturday;

    @BindView(R.id.sw_options_nextday8pm)
    Switch swNextDayAt8pm;

    @BindView(R.id.et_options_labgroups)
    EditText etGroupFilter;

    private ArrayAdapter<Programme> progAdapter;

    private ArrayAdapter<Year> yearAdapter;

    private Programmes programmes;

    private ProgrammeType checkedType;

    private boolean wasYearInitialized = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ButterKnife.bind(this);

        programmes = App.getProgrammes();
        rgProgType.setOnCheckedChangeListener(getCheckedChangeListener());
        spnProg.setOnItemSelectedListener(getOnItemSelectedListener());

        initViewsFromPrefs();
    }

    private RadioGroup.OnCheckedChangeListener getCheckedChangeListener(){
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setCheckedType(checkedId);
                setProgSpinnerData(checkedType);
            }
        };
    }

    private AdapterView.OnItemSelectedListener getOnItemSelectedListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setYearSpinnerData(checkedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void initViewsFromPrefs() {
        initRadioGroupFromPrefs();
        initProgSpinnerFromPrefs();
        //year spinner gets initialized when programme is first picked.
        initOtherOptionsFromPrefs();
        initGroupFilterFromPrefs();
    }

    private void setProgSpinnerData(ProgrammeType type) {
        progAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                programmes.getProgrammesByType(type)
        );

        spnProg.setAdapter(progAdapter);
    }

    private void setYearSpinnerData(ProgrammeType type) {
        List<Year> years = new ArrayList<>();

        if(type == ProgrammeType.PROF){
            //this is a special case because there are two courses that differ in year count.
            //'Elektrotehnika - racunarstvo' only has a FIRST year, while 'Elektrotehnika - informatika'
            //only has SECOND and THIRD year. we need to update the spinner accordingly.

            Programme selected = progAdapter.getItem(spnProg.getSelectedItemPosition());

            if(selected.getId().equals("53")){
                //53 is the ID of 'Elektrotehnika - racunarstvo' which only has
                //one year - the first.
                years.add(Year.FIRST);
            } else if(selected.getId().equals("7")){
                //7 is the ID of 'Elektrotehnika - informatika' which only has
                //two years - the second and the third.
                years.add(Year.SECOND);
                years.add(Year.THIRD);
            } else {
                //for any other programme of type PROF, get years as you would normally.
                years = Arrays.asList(ProgrammeType.PROF.getYears());
            }
        } else {
            years = Arrays.asList(type.getYears());
        }

        yearAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                years
        );

        spnYear.setAdapter(yearAdapter);

        if(!wasYearInitialized){
            initYearSpinnerFromPrefs();
            wasYearInitialized = true;
        }
    }

    private void initRadioGroupFromPrefs(){
        int prevChecked = SharedPrefsUtil.get(this, Constants.CHECKED_PROGTYPE_ID_KEY, R.id.rb_options_undergrad);
        rgProgType.check(prevChecked);
    }

    private void initYearSpinnerFromPrefs(){
        int prevPos = SharedPrefsUtil.get(this, Constants.CHECKED_YEAR_POS_KEY, 0);
        spnYear.setSelection(prevPos);
    }

    private void initProgSpinnerFromPrefs(){
        int prevPos = SharedPrefsUtil.get(this, Constants.CHECKED_PROG_POS_KEY, 0);
        spnProg.setSelection(prevPos);
    }

    private void initOtherOptionsFromPrefs() {
        swSkipSaturday.setChecked(SharedPrefsUtil.get(this, Constants.SKIP_SATURDAY_KEY, false));
        swNextDayAt8pm.setChecked(SharedPrefsUtil.get(this, Constants.NEXTDAY_AFTER_8PM_KEY, false));
    }

    private void initGroupFilterFromPrefs(){
        etGroupFilter.setText(SharedPrefsUtil.get(this, Constants.GROUP_FILTER_KEY, ""));
    }

    @OnClick(R.id.btn_options_save)
    void saveOptions(){
        saveOptionsToPrefs();
        startScheduleActivity();
    }

    private void setCheckedType(int checkedId) {
        ProgrammeType type = ProgrammeType.UNDERGRAD;

        switch(checkedId){
            case R.id.rb_options_grad: type = ProgrammeType.GRAD; break;
            case R.id.rb_options_prof: type = ProgrammeType.PROF; break;
            case R.id.rb_options_diff: type = ProgrammeType.DIFF; break;
            default: break;
        }

        checkedType = type;
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
        SharedPrefsUtil.save(this, Constants.CHECKED_PROGTYPE_ID_KEY, rgProgType.getCheckedRadioButtonId());
        SharedPrefsUtil.save(this, Constants.CHECKED_PROG_POS_KEY, spnProg.getSelectedItemPosition());
        SharedPrefsUtil.save(this, Constants.CHECKED_YEAR_POS_KEY, spnYear.getSelectedItemPosition());
    }

    private void startScheduleActivity() {
        startActivity(new Intent(this, ScheduleActivity.class));
    }
}
