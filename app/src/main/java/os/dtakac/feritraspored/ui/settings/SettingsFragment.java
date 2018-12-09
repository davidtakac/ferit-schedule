package os.dtakac.feritraspored.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.model.Time24Hour;
import os.dtakac.feritraspored.model.repository.IRepository;
import os.dtakac.feritraspored.model.repository.SharedPrefsRepository;
import os.dtakac.feritraspored.ui.timepicker.TimePickerFragment;
import os.dtakac.feritraspored.ui.timepicker.TimeSetListener;
import os.dtakac.feritraspored.util.Constants;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private ListPreference progTypeList;
    private ListPreference programmeList;
    private ListPreference yearList;
    private Preference timePickerPref;
    private SwitchPreference skipDay;
    private EditTextPreference groupsPref;

    private IRepository repo;

    private boolean wasProgrammeInitialized = false;
    private boolean wasYearInitialized = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        p.registerOnSharedPreferenceChangeListener(this);
        repo = new SharedPrefsRepository(p);
    }

    @Override
    public void onCreatePreferencesFix(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.fragment_preference, s);

        initPrefReferences();
        initPreferenceLists();
        initTimePickerPref();
        initGroupsPref();
    }

    @Override
    public void onPause() {
        if(getActivity() !=null) {
            Fragment timepicker = getActivity().getSupportFragmentManager().findFragmentByTag(Constants.TIMEPICKER_KEY);
            if (timepicker != null) {
                DialogFragment df = (DialogFragment) timepicker;
                df.dismiss();
            }
        }
        super.onPause();
    }

    private void initPrefReferences() {
        PreferenceManager m = getPreferenceManager();
        progTypeList = (ListPreference) m.findPreference(getStr(R.string.prefkey_progtype));
        programmeList = (ListPreference) m.findPreference(getStr(R.string.prefkey_programme));
        yearList = (ListPreference) m.findPreference(getStr(R.string.prefkey_year));
        timePickerPref = m.findPreference(getStr(R.string.prefkey_time));
        skipDay = (SwitchPreference) m.findPreference(getStr(R.string.prefkey_skipday));
        groupsPref = (EditTextPreference) m.findPreference(getStr(R.string.prefkey_groups));
    }

    private void initPreferenceLists(){
        setUpProgrammeTypeList();

        int progTypeValue = Integer.parseInt(progTypeList.getValue());
        setUpProgrammeList(progTypeValue);

        int programmeValue = Integer.parseInt(programmeList.getValue());
        setUpYearList(progTypeValue, programmeValue);
    }

    private void initTimePickerPref(){
        timePickerPref.setOnPreferenceClickListener(this);
        timePickerPref.setSummary(
                new Time24Hour(
                        repo.get(getStr(R.string.prefkey_time_hour), 20), repo.get(getStr(R.string.prefkey_time_minute), 0))
                        .toString()
        );
        timePickerPref.setEnabled(skipDay.isChecked());
    }

    private void initGroupsPref(){
        groupsPref.getEditText().setHint(getStr(R.string.settings_grouphighlight_hint));
        setGroupsSummaryFromPrefs();
    }

    private void setGroupsSummaryFromPrefs(){
        String summary = repo.get(getStr(R.string.prefkey_groups), getStr(R.string.settings_grouphighlight_empty));
        groupsPref.setSummary(summary.isEmpty() ? getStr(R.string.settings_grouphighlight_empty) : summary);
    }

    private void setUpProgrammeTypeList(){
        progTypeList.setEntries(getStrArray(R.array.programmetype_names));
        progTypeList.setEntryValues(getStrArray(R.array.programmetype_values));
        progTypeList.setValue(repo.get(getStr(R.string.prefkey_progtype), "1"));
    }

    private void setUpProgrammeList(int selectedProgTypeId) {
        int entriesId = R.array.undergrad_names;
        int valuesId = R.array.undergrad_values;

        switch (selectedProgTypeId){
            case 2: {
                entriesId = R.array.graduate_names;
                valuesId = R.array.graduate_values;
                break;
            }
            case 3: {
                entriesId = R.array.professional_names;
                valuesId = R.array.professional_values;
                break;
            }
            case 4: {
                entriesId = R.array.differential_names;
                valuesId = R.array.differential_values;
                break;
            }
            default: break;
        }

        programmeList.setEntries(getStrArray(entriesId));
        programmeList.setEntryValues(getStrArray(valuesId));

        if(!wasProgrammeInitialized){
            int valueIndex = programmeList.findIndexOfValue(repo.get(getStr(R.string.prefkey_programme), ""));
            programmeList.setValueIndex(valueIndex != -1 ? valueIndex : 0);
            wasProgrammeInitialized = true;
        }
    }

    private void setUpYearList(int selectedProgTypeId, int selectedProgrammeId){
        int entriesId = R.array.year_undergrad_names;
        int valuesId = R.array.year_undergrad_values;

        switch (selectedProgTypeId){
            case 2:{
                entriesId = R.array.year_graduate_names;
                valuesId = R.array.year_graduate_values;
                break;
            }
            case 4:{
                entriesId = R.array.year_differential_names;
                valuesId = R.array.year_differential_values;
                break;
            }
            case 3:{
                if(selectedProgrammeId == 53){
                    entriesId = R.array.year_ETRAC_professional_names;
                    valuesId = R.array.year_ETRAC_professional_values;
                } else if(selectedProgrammeId == 7){
                    entriesId = R.array.year_ETINF_professional_names;
                    valuesId = R.array.year_ETINF_professional_values;
                } else {
                    entriesId = R.array.year_professional_names;
                    valuesId = R.array.year_professional_values;
                }
                break;
            }
            default: break;
        }

        yearList.setEntries(getStrArray(entriesId));
        yearList.setEntryValues(getStrArray(valuesId));

        if(!wasYearInitialized){
            int valueIndex = yearList.findIndexOfValue(repo.get(getStr(R.string.prefkey_year), ""));
            yearList.setValueIndex(valueIndex != -1 ? valueIndex : 0);
            wasYearInitialized = true;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(Constants.LOG_TAG, key);
        if(key.equals(getStr(R.string.prefkey_progtype))){
            setUpProgrammeList(Integer.parseInt(progTypeList.getValue()));
            programmeList.setValueIndex(0);
        } else if(key.equals(getStr(R.string.prefkey_programme))){
            setUpYearList(Integer.parseInt(progTypeList.getValue()), Integer.parseInt(programmeList.getValue()));
            yearList.setValueIndex(0);
        } else if(key.equals(getStr(R.string.prefkey_skipday))){
            timePickerPref.setEnabled(skipDay.isChecked());
        } else if(key.equals(getStr(R.string.prefkey_groups))){
            setGroupsSummaryFromPrefs();
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equals(getStr(R.string.prefkey_time))){
            Time24Hour prevTime = new Time24Hour(repo.get(getStr(R.string.prefkey_time_hour), 20), repo.get(getStr(R.string.prefkey_time_minute), 0));

            DialogFragment f = TimePickerFragment.newInstance(prevTime, new TimeSetListener() {
                @Override
                public void onTimeSet(Time24Hour setTime) {
                    repo.add(getStr(R.string.prefkey_time_hour), setTime.getHour());
                    repo.add(getStr(R.string.prefkey_time_minute), setTime.getMinute());
                    timePickerPref.setSummary(setTime.toString());
                }
            });
            if(getActivity() != null){
                f.show(getActivity().getSupportFragmentManager(), Constants.TIMEPICKER_KEY);
            }
        }
        return true;
    }

    private String getStr(int id){
        if(getActivity() != null){
            return getActivity().getResources().getString(id);
        }
        return null;
    }

    private String[] getStrArray(int id){
        if(getActivity() != null){
            return getActivity().getResources().getStringArray(id);
        }
        return null;
    }
}
