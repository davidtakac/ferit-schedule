package os.dtakac.feritraspored.settings.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.common.PrefsRepository;
import os.dtakac.feritraspored.views.groups.AlertDialogFragment;
import os.dtakac.feritraspored.views.timepicker.Time24Hour;
import os.dtakac.feritraspored.views.timepicker.TimePickerFragment;
import os.dtakac.feritraspored.views.timepicker.TimeSetListener;
import os.dtakac.feritraspored.common.Constants;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private ListPreference progTypeList, programmeList, yearList, themeList;
    private EditTextPreference groupsPref;
    private Preference groupsHelpPref, timePickerPref, changelogPref, bugReportPref;

    private PrefsRepository prefs;
    private SharedPreferences defaultSharedPreferences;

    private boolean wasProgrammeInitialized = false;
    private boolean wasYearInitialized = false;
    private boolean werePrefsModified = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefs = new PrefsRepository(defaultSharedPreferences, getResources());
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.fragment_preference, s);

        initPrefReferences();
        initPreferenceLists();
        initTimePickerPref();
        initGroupsPref();
        initChangelogPref();
        initBugReportPref();
    }

    @Override
    public void onResume() {
        super.onResume();
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
        prefs.add(R.string.key_settings_modified, werePrefsModified);
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private void initPrefReferences() {
        PreferenceManager m = getPreferenceManager();
        progTypeList = m.findPreference(getStr(R.string.key_programme_type));
        programmeList = m.findPreference(getStr(R.string.key_programme));
        yearList = m.findPreference(getStr(R.string.key_year));
        timePickerPref = m.findPreference(getStr(R.string.key_time));
        groupsPref = m.findPreference(getStr(R.string.key_groups));
        groupsHelpPref = m.findPreference(getStr(R.string.key_groups_help));
        themeList = m.findPreference(getStr(R.string.key_theme));
        changelogPref = m.findPreference(getStr(R.string.key_changelog));
        bugReportPref = m.findPreference(getStr(R.string.key_report_bug));
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
        setTimePickerEnabled(prefs.get(R.string.key_skip_day, false));
    }

    private void initChangelogPref(){
        changelogPref.setOnPreferenceClickListener(this);
    }

    private void initBugReportPref(){
        bugReportPref.setOnPreferenceClickListener(this);
    }

    private void setTimePickerSummaryFromPrefs(){
        int prevHour = prefs.get(R.string.key_time_hour, 20);
        int prevMinute = prefs.get(R.string.key_time_minute, 0);
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
        groupsPref.setOnBindEditTextListener(editText -> editText.setHint(getStr(R.string.hint_group_highlight)));
        groupsHelpPref.setOnPreferenceClickListener(this);
        setGroupsSummaryFromPrefs();
        setGroupsPreferenceEnabled(prefs.get(R.string.key_groups_toggle, false));
    }

    private void setGroupsSummaryFromPrefs(){
        String summary = prefs.get(R.string.key_groups, getStr(R.string.placeholder_group_highlight_empty));
        groupsPref.setSummary(summary.isEmpty() ? getStr(R.string.placeholder_group_highlight_empty) : summary);
    }

    private void setUpProgrammeTypeList(){
        progTypeList.setEntries(getStrArray(R.array.names_programme_type));
        progTypeList.setEntryValues(getStrArray(R.array.values_programme_type));
        progTypeList.setValue(prefs.get(R.string.key_programme_type, "1"));
    }

    private void setProgrammeValue(int index){
        programmeList.setValueIndex(index);
    }

    private void setYearValue(int index){
        yearList.setValueIndex(index);
    }

    private void setUpProgrammeList(int selectedProgTypeId) {
        int entriesId = R.array.names_undergrad;
        int valuesId = R.array.values_undergrad;

        switch (selectedProgTypeId){
            case 2: {
                entriesId = R.array.names_grad;
                valuesId = R.array.values_grad;
                break;
            }
            case 3: {
                entriesId = R.array.names_professional;
                valuesId = R.array.values_professional;
                break;
            }
            case 4: {
                entriesId = R.array.names_differential;
                valuesId = R.array.values_differential;
                break;
            }
            default: break;
        }

        programmeList.setEntries(getStrArray(entriesId));
        programmeList.setEntryValues(getStrArray(valuesId));

        if(!wasProgrammeInitialized){
            int valueIndex = programmeList.findIndexOfValue(prefs.get(R.string.key_programme, ""));
            setProgrammeValue(valueIndex != -1 ? valueIndex : 0);
            wasProgrammeInitialized = true;
        }
    }

    private void setUpYearList(int selectedProgTypeId, int selectedProgrammeId){
        int entriesId = R.array.names_years_undergrad;
        int valuesId = R.array.values_years_undergrad;

        switch (selectedProgTypeId){
            case 1:{
                if(selectedProgrammeId == 21){
                    entriesId = R.array.names_years_ETKINF;
                    valuesId = R.array.values_years_ETKINF;
                } else {
                    entriesId = R.array.names_years_undergrad;
                    valuesId = R.array.values_years_undergrad;
                }
                break;
            }
            case 2:{
                entriesId = R.array.names_years_grad;
                valuesId = R.array.values_years_grad;
                break;
            }
            case 3:{
                if(selectedProgrammeId == 53){
                    entriesId = R.array.names_years_ETRAC;
                    valuesId = R.array.values_years_ETRAC;
                } else if(selectedProgrammeId == 7){
                    entriesId = R.array.names_years_ETINF;
                    valuesId = R.array.values_years_ETINF;
                } else {
                    entriesId = R.array.names_years_professional;
                    valuesId = R.array.values_years_professional;
                }
                break;
            }
            case 4:{
                entriesId = R.array.names_years_differential;
                valuesId = R.array.values_years_differential;
                break;
            }
            default: break;
        }

        yearList.setEntries(getStrArray(entriesId));
        yearList.setEntryValues(getStrArray(valuesId));

        if(!wasYearInitialized){
            int valueIndex = yearList.findIndexOfValue(prefs.get(R.string.key_year, ""));
            setYearValue(valueIndex != -1 ? valueIndex : 0);
            wasYearInitialized = true;
        }
    }

    private void setupThemeList(){
        String theme = prefs.get(R.string.key_theme, getStrArray(R.array.theme_options)[0]);
        themeList.setValue(theme);
    }

    private void setTheme(){
        String themeStr = prefs.get(R.string.key_theme, getStrArray(R.array.theme_options)[0]);
        AppCompatDelegate.setDefaultNightMode(Integer.parseInt(themeStr));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(!werePrefsModified && !key.equals(getStr(R.string.key_prev_week))) {
            werePrefsModified = true;
        }

        if(key.equals(getStr(R.string.key_programme_type))){
            setUpProgrammeList(Integer.parseInt(progTypeList.getValue()));
            setProgrammeValue(0);
        } else if(key.equals(getStr(R.string.key_programme))){
            setUpYearList(Integer.parseInt(progTypeList.getValue()), Integer.parseInt(programmeList.getValue()));
            setYearValue(0);
        } else if(key.equals(getStr(R.string.key_skip_day))){
            setTimePickerEnabled(prefs.get(R.string.key_skip_day,false));
        } else if(key.equals(getStr(R.string.key_groups))){
            setGroupsSummaryFromPrefs();
        } else if(key.equals(getStr(R.string.key_groups_toggle))){
            setGroupsPreferenceEnabled(prefs.get(R.string.key_groups_toggle, false));
        } else if(key.equals(getStr(R.string.key_theme))){
            setTheme();
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if(key.equals(getStr(R.string.key_time))){
            showTimePicker();
        } else if(key.equals(getStr(R.string.key_groups_help))){
            showGroupsHelp();
        } else if(key.equals(getStr(R.string.key_changelog))){
            showChangelog();
        } else if(key.equals(getStr(R.string.key_report_bug))){
            sendBugReport();
        }
        return true;
    }

    private void showTimePicker(){
        Time24Hour prevTime = new Time24Hour(prefs.get(R.string.key_time_hour, 20), prefs.get(R.string.key_time_minute, 0));

        DialogFragment f = TimePickerFragment.newInstance(prevTime, (TimeSetListener) setTime -> {
            prefs.add(R.string.key_time_hour, setTime.getHour());
            prefs.add(R.string.key_time_minute, setTime.getMinute());
            setTimePickerSummaryFromPrefs();
        });

        if(getActivity() != null){
            f.show(getActivity().getSupportFragmentManager(), Constants.TIMEPICKER_KEY);
        }
    }

    private void showGroupsHelp(){
        DialogFragment f = AlertDialogFragment.newInstance(R.string.title_groups_help, R.string.content_groups_help, R.string.label_groups_help_confirm);
        if(getActivity() != null){
            f.show(getActivity().getSupportFragmentManager(), Constants.GROUPS_HELP_KEY);
        }
    }

    private void showChangelog(){
        DialogFragment f = AlertDialogFragment.newInstance(R.string.title_whats_new, R.string.content_whats_new, R.string.dismiss_whats_new);
        if(getActivity() != null){
            f.show(getActivity().getSupportFragmentManager(), Constants.WHATS_NEW_KEY);
        }
    }

    private void sendBugReport(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, getStrArray(R.array.email_addresses));
        intent.putExtra(Intent.EXTRA_SUBJECT, getStr(R.string.subject_bug_report));
        startActivity(Intent.createChooser(intent, getStr(R.string.label_email_via)));
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
