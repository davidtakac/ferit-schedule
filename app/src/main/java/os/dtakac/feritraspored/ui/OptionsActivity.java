package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import os.dtakac.feritraspored.App;
import os.dtakac.feritraspored.model.Time24Hour;
import os.dtakac.feritraspored.ui.TimePicker.TimePickerFragment;
import os.dtakac.feritraspored.ui.TimePicker.TimeSetListener;
import os.dtakac.feritraspored.presenter.options.OptionsContract;
import os.dtakac.feritraspored.presenter.options.OptionsPresenter;
import os.dtakac.feritraspored.model.programmes.Programme;
import os.dtakac.feritraspored.model.repository.SharedPrefsRepository;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.model.year.Year;

public class OptionsActivity extends AppCompatActivity implements OptionsContract.View {

    @BindView(R.id.rg_options_pickprogtype)
    RadioGroup rgProgType;

    @BindView(R.id.spn_options_pickprogramme)
    Spinner spnProg;

    @BindView(R.id.spn_options_pickyear)
    Spinner spnYear;

    @BindView(R.id.sw_options_skipsat)
    Switch swSkipSaturday;

    @BindView(R.id.sw_options_nextday)
    Switch swNextDay;

    @BindView(R.id.et_options_labgroups)
    EditText etGroupFilter;

    @BindView(R.id.btn_options_timepicker)
    Button btnTimePicker;

    private OptionsContract.Presenter presenter;

    private ArrayAdapter<Programme> progAdapter;

    private ArrayAdapter<Year> yearAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ButterKnife.bind(this);

        presenter = new OptionsPresenter(
                this,
                new SharedPrefsRepository(PreferenceManager.getDefaultSharedPreferences(this)),
                App.getProgrammes()
        );

        initViews();
        presenter.initViewValues();
    }

    private void initViews() {
        rgProgType.setOnCheckedChangeListener(getCheckedChangeListener());
        spnProg.setOnItemSelectedListener(getOnItemSelectedListener());
        swNextDay.setOnCheckedChangeListener(getTimeSwitchChangedListener());
    }

    private RadioGroup.OnCheckedChangeListener getCheckedChangeListener(){
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                presenter.setProgSpinnerData(checkedId);
            }
        };
    }

    private AdapterView.OnItemSelectedListener getOnItemSelectedListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setYearSpinnerData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener getTimeSwitchChangedListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTimePickerButtonEnabled(isChecked);
            }
        };
    }

    @OnClick(R.id.fab_options_save)
    void saveOptions(){
        presenter.saveOptions();
        startScheduleActivity();
    }

    @OnClick(R.id.btn_options_timepicker)
    void showTimePicker(){
        Time24Hour prevSelectedTime = presenter.getSelectedTime();

        DialogFragment f = TimePickerFragment.newInstance(prevSelectedTime, new TimeSetListener() {
            @Override
            public void onTimeSet(Time24Hour setTime) {
                presenter.setSelectedTime(setTime);
            }
        });
        f.show(getSupportFragmentManager(), "TimePicker");
    }

    @Override
    public void setCheckedRadioButton(int radioButtonId) {
        rgProgType.check(radioButtonId);
    }

    @Override
    public void setYear(int position) {
        spnYear.setSelection(position);
    }

    @Override
    public void setProgramme(int position) {
        spnProg.setSelection(position);
    }

    @Override
    public void setSkipSaturdayChecked(boolean isChecked) {
        swSkipSaturday.setChecked(isChecked);
    }

    @Override
    public void setNextDayChecked(boolean isChecked) {
        swNextDay.setChecked(isChecked);
    }

    @Override
    public void setGroupFilterText(String text) {
        etGroupFilter.setText(text);
    }

    @Override
    public void setProgSpinnerData(List<Programme> data) {
        progAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                data
        );
        spnProg.setAdapter(progAdapter);
    }

    @Override
    public void setYearSpinnerData(List<Year> data) {
        yearAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                data
        );
        spnYear.setAdapter(yearAdapter);
    }

    @Override
    public void setTimePickerButtonText(String time) {
        btnTimePicker.setText(time);
    }

    @Override
    public void setTimePickerButtonEnabled(boolean isEnabled) {
        Resources r = getResources();
        btnTimePicker.setEnabled(isEnabled);
        btnTimePicker.setTextColor(isEnabled ? r.getColor(R.color.darkGreen) : r.getColor(R.color.grey));
    }

    @Override
    public Programme getSelectedProgramme() {
        return progAdapter.getItem(getProgSpinnerPosition());
    }

    @Override
    public Year getSelectedYear() {
        return yearAdapter.getItem(getYearSpinnerPosition());
    }

    @Override
    public boolean getSkipSaturdayOption() {
        return swSkipSaturday.isChecked();
    }

    @Override
    public boolean getNextDayOption() {
        return swNextDay.isChecked();
    }

    @Override
    public String getGroupFilterText() {
        return etGroupFilter.getText().toString();
    }

    @Override
    public int getCheckedRbId() {
        return rgProgType.getCheckedRadioButtonId();
    }

    @Override
    public int getProgSpinnerPosition() {
        return spnProg.getSelectedItemPosition();
    }

    @Override
    public int getYearSpinnerPosition() {
        return spnYear.getSelectedItemPosition();
    }

    private void startScheduleActivity() {
        startActivity(new Intent(this, ScheduleActivity.class));
    }
}
