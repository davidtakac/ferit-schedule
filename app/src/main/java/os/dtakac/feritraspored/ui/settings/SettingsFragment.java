package os.dtakac.feritraspored.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Arrays;
import java.util.Collections;

import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.model.repository.IRepository;
import os.dtakac.feritraspored.model.repository.SharedPrefsRepository;
import os.dtakac.feritraspored.ui.groups.GroupsHelpDialogFragment;
import os.dtakac.feritraspored.ui.timepicker.Time24Hour;
import os.dtakac.feritraspored.ui.timepicker.TimePickerFragment;
import os.dtakac.feritraspored.ui.timepicker.TimeSetListener;
import os.dtakac.feritraspored.util.Constants;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private ListPreference progTypeList;
    private ListPreference programmeList;
    private ListPreference yearList;
    private ListPreference themeList;
    private Preference timePickerPref;
    private EditTextPreference groupsPref;
    private Preference groupsHelpPref;

    private IRepository repo;
    private SharedPreferences prefs;

    private boolean wasProgrammeInitialized = false;
    private boolean wasYearInitialized = false;

    private boolean werePrefsModified = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        repo = new SharedPrefsRepository(prefs);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.fragment_preference, s);

        initPrefReferences();
        initPreferenceLists();
        initTimePickerPref();
        initGroupsPref();
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(this);
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
        repo.add(getStr(R.string.prefkey_settings_modified), werePrefsModified);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private void initPrefReferences() {
        PreferenceManager m = getPreferenceManager();

        progTypeList = m.findPreference(getStr(R.string.prefkey_progtype));
        programmeList = m.findPreference(getStr(R.string.prefkey_programme));
        yearList = m.findPreference(getStr(R.string.prefkey_year));
        timePickerPref = m.findPreference(getStr(R.string.prefkey_time));
        groupsPref = m.findPreference(getStr(R.string.prefkey_groups));
        groupsHelpPref = m.findPreference(getStr(R.string.prefkey_groupshelp));
        themeList = m.findPreference(getStr(R.string.prefkey_theme));
    }

    private void initPreferenceLists(){
        setUpProgrammeTypeList();

        int progTypeValue = Integer.parseInt(progTypeList.getValue());
        setUpProgrammeList(progTypeValue);

        int programmeValue = Integer.parseInt(programmeList.getValue());
        setUpYearList(progTypeValue, programmeValue);

        setupThemeList();
    }

    private void initTimePickerPref(){
        timePickerPref.setOnPreferenceClickListener(this);

        setTimePickerSummaryFromPrefs();
        setTimePickerEnabled(repo.get(getStr(R.string.prefkey_skipday), false));
    }

    private void setTimePickerSummaryFromPrefs(){
        int prevHour = repo.get(getStr(R.string.prefkey_time_hour), 20);
        int prevMinute = repo.get(getStr(R.string.prefkey_time_minute), 0);
        timePickerPref.setSummary(new Time24Hour(prevHour, prevMinute).toString());
    }

    private void setTimePickerEnabled(boolean isEnabled){
        timePickerPref.setEnabled(isEnabled);
    }

    private void setGroupsPreferenceEnabled(boolean isEnabled){
        groupsPref.setEnabled(isEnabled);
        groupsHelpPref.setEnabled(isEnabled);
    }

    private void initGroupsPref(){
        groupsPref.setOnBindEditTextListener(editText -> editText.setHint(getStr(R.string.settings_grouphighlight_hint)));
        groupsHelpPref.setOnPreferenceClickListener(this);
        setGroupsSummaryFromPrefs();
        setGroupsPreferenceEnabled(repo.get(getStr(R.string.prefkey_groups_toggle), false));
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

    private void setProgrammeValue(int index){
        programmeList.setValueIndex(index);
    }

    private void setYearValue(int index){
        yearList.setValueIndex(index);
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
            setProgrammeValue(valueIndex != -1 ? valueIndex : 0);
            wasProgrammeInitialized = true;
        }
    }

    private void setUpYearList(int selectedProgTypeId, int selectedProgrammeId){
        int entriesId = R.array.year_undergrad_names;
        int valuesId = R.array.year_undergrad_values;

        switch (selectedProgTypeId){
            case 1:{
                if(selectedProgrammeId == 21){
                    entriesId = R.array.year_ETKINF_undergrad_names;
                    valuesId = R.array.year_ETKINF_undergrad_values;
                } else {
                    entriesId = R.array.year_undergrad_names;
                    valuesId = R.array.year_undergrad_values;
                }
                break;
            }
            case 2:{
                entriesId = R.array.year_graduate_names;
                valuesId = R.array.year_graduate_values;
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
            case 4:{
                entriesId = R.array.year_differential_names;
                valuesId = R.array.year_differential_values;
                break;
            }
            default: break;
        }

        yearList.setEntries(getStrArray(entriesId));
        yearList.setEntryValues(getStrArray(valuesId));

        if(!wasYearInitialized){
            int valueIndex = yearList.findIndexOfValue(repo.get(getStr(R.string.prefkey_year), ""));
            setYearValue(valueIndex != -1 ? valueIndex : 0);
            wasYearInitialized = true;
        }
    }

    private void setupThemeList(){
        String theme = repo.get(getStr(R.string.prefkey_theme), getStrArray(R.array.theme_options)[0]);
        themeList.setValue(theme);
    }

    private void setTheme(){
        String themeStr = repo.get(getStr(R.string.prefkey_theme), getStrArray(R.array.theme_options)[0]);
        AppCompatDelegate.setDefaultNightMode(Integer.parseInt(themeStr));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(!werePrefsModified && !key.equals(getStr(R.string.prefkey_prevdisplayedweek))) {
            werePrefsModified = true;
        }

        if(key.equals(getStr(R.string.prefkey_progtype))){
            setUpProgrammeList(Integer.parseInt(progTypeList.getValue()));
            setProgrammeValue(0);
        } else if(key.equals(getStr(R.string.prefkey_programme))){
            setUpYearList(Integer.parseInt(progTypeList.getValue()), Integer.parseInt(programmeList.getValue()));
            setYearValue(0);
        } else if(key.equals(getStr(R.string.prefkey_skipday))){
            setTimePickerEnabled(repo.get(key,false));
        } else if(key.equals(getStr(R.string.prefkey_groups))){
            setGroupsSummaryFromPrefs();
        } else if(key.equals(getStr(R.string.prefkey_groups_toggle))){
            setGroupsPreferenceEnabled(repo.get(key, false));
        } else if(key.equals(getStr(R.string.prefkey_theme))){
            setTheme();
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if(key.equals(getStr(R.string.prefkey_time))){
            Time24Hour prevTime = new Time24Hour(repo.get(getStr(R.string.prefkey_time_hour), 20), repo.get(getStr(R.string.prefkey_time_minute), 0));

            DialogFragment f = TimePickerFragment.newInstance(prevTime, (TimeSetListener) setTime -> {
                repo.add(getStr(R.string.prefkey_time_hour), setTime.getHour());
                repo.add(getStr(R.string.prefkey_time_minute), setTime.getMinute());
                setTimePickerSummaryFromPrefs();
            });

            if(getActivity() != null){
                f.show(getActivity().getSupportFragmentManager(), Constants.TIMEPICKER_KEY);
            }

        } else if(key.equals(getStr(R.string.prefkey_groupshelp))){
            DialogFragment f = new GroupsHelpDialogFragment();
            if(getActivity() != null){
                f.show(getActivity().getSupportFragmentManager(), Constants.GROUPSHELP_KEY);
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
