package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import os.dtakac.feritraspored.App;
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

    @BindView(R.id.sw_options_nextday8pm)
    Switch swNextDayAfter8pm;

    @BindView(R.id.et_options_labgroups)
    EditText etGroupFilter;

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

    @OnClick(R.id.btn_options_save)
    void saveOptions(){
        presenter.saveOptions();
        startScheduleActivity();
    }

    @Override
    public void checkRadioButton(int radioButtonId) {
        rgProgType.check(radioButtonId);
    }

    @Override
    public void selectYear(int position) {
        spnYear.setSelection(position);
    }

    @Override
    public void selectProgramme(int position) {
        spnProg.setSelection(position);
    }

    @Override
    public void checkSkipSaturdayOption(boolean isChecked) {
        swSkipSaturday.setChecked(isChecked);
    }

    @Override
    public void checkNextDayAfter8pmOption(boolean isChecked) {
        swNextDayAfter8pm.setChecked(isChecked);
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
    public boolean getNextDayAfter8pmOption() {
        return swNextDayAfter8pm.isChecked();
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
