package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

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
public class ProgrammeYearPickerActivity extends AppCompatActivity {

    @BindView(R.id.rg_progyearpick_pickprogtype)
    RadioGroup rgProgType;

    @BindView(R.id.spn_progyearpick_pickprogramme)
    Spinner spnProg;

    @BindView(R.id.spn_progyearpick_pickyear)
    Spinner spnYear;

    @BindView(R.id.btn_progyearpick_save)
    Button btnSave;

    private ArrayAdapter<Programme> progAdapter;

    private ArrayAdapter<Year> yearAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progyearpick);
        ButterKnife.bind(this);

        setSpinnersEnabled(false);
        setSaveEnabled(false);

        rgProgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setSpinnersEnabled(true);
                setSaveEnabled(true);
                setSpinnerDataBasedOnId(checkedId);
            }
        });
    }

    @OnClick(R.id.btn_progyearpick_save)
    void savePickToPrefs(){
        SharedPrefsUtil.save(this, Constants.PROGRAMME_KEY, progAdapter.getItem(spnProg.getSelectedItemPosition()).getId());
        SharedPrefsUtil.save(this, Constants.YEAR_KEY, yearAdapter.getItem(spnYear.getSelectedItemPosition()).getId());

        startScheduleActivity();
    }

    private void setSpinnerDataBasedOnId(int checkedId) {
        ProgrammeType type = ProgrammeType.UNDERGRAD;

        switch(checkedId){
            case R.id.rb_progyearpick_grad: type = ProgrammeType.GRAD; break;
            case R.id.rb_progyearpick_prof: type = ProgrammeType.PROF; break;
            case R.id.rb_progyearpick_diff: type = ProgrammeType.DIFF; break;
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

    public void setSaveEnabled(boolean isEnabled) {
        btnSave.setEnabled(isEnabled);
    }
}
