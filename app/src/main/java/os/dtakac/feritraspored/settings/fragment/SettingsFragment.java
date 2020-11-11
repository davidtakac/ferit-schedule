package os.dtakac.feritraspored.settings.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.common.constants.ConstantsKt;
import os.dtakac.feritraspored.common.preferences.PreferenceRepository;
import os.dtakac.feritraspored.common.preferences.PreferenceRepositoryImpl;
import os.dtakac.feritraspored.common.resources.ResourceRepositoryImpl;
import os.dtakac.feritraspored.views.groups.AlertDialogFragment;
import os.dtakac.feritraspored.views.timepicker.Time24Hour;
import os.dtakac.feritraspored.views.timepicker.TimePickerFragmentOld;
import os.dtakac.feritraspored.views.timepicker.TimeSetListener;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private ListPreference themeList;
    private EditTextPreference filters,
            courseIdentifier;
    private Preference filtersHelp,
            timePicker,
            changelog,
            bugReport,
            courseIdentifierHelp;

    private PreferenceRepository prefs;
    private SharedPreferences defaultSharedPreferences;

    private boolean werePrefsModified = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefs = new PreferenceRepositoryImpl(new ResourceRepositoryImpl(getResources()), defaultSharedPreferences);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.fragment_preference, s);
        initPreferences();
        initPreferenceLists();
        initTimePicker();
        initFilters();
        initChangelog();
        initBugReport();
        initCourseIdentifier();
        initCourseIdentifierHelp();
    }

    @Override
    public void onResume() {
        super.onResume();
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        if(getActivity() !=null) {
            Fragment timepicker = getActivity().getSupportFragmentManager().findFragmentByTag(ConstantsKt.DIALOG_TIME_PICKER);
            if (timepicker != null) {
                DialogFragment df = (DialogFragment) timepicker;
                df.dismiss();
            }
        }
        prefs.setSettingsModified(werePrefsModified);
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(!werePrefsModified && !key.equals(getString(R.string.key_prev_week))) {
            werePrefsModified = true;
        }

        if(key.equals(getString(R.string.key_skip_day))){
            setTimePickerEnabled(prefs.isSkipDay());
        } else if(key.equals(getString(R.string.key_groups))){
            setGroupsSummaryFromPrefs();
        } else if(key.equals(getString(R.string.key_groups_toggle))){
            setGroupsPreferenceEnabled(prefs.isFiltersEnabled());
        } else if(key.equals(getString(R.string.key_theme))){
            setTheme();
        } else if(key.equals(getString(R.string.key_course_identifier))) {
            setCourseIdentifierSummaryFromPrefs();
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if(key.equals(getString(R.string.key_time))){
            showTimePicker();
        } else if(key.equals(getString(R.string.key_groups_help))){
            showGroupsHelp();
        } else if(key.equals(getString(R.string.key_changelog))){
            showChangelog();
        } else if(key.equals(getString(R.string.key_report_bug))){
            sendBugReport();
        } else if(key.equals(getString(R.string.key_course_identifier_help))) {
            showCourseIdentifierHelp();
        }
        return true;
    }

    private void initPreferences() {
        PreferenceManager m = getPreferenceManager();
        timePicker = m.findPreference(getString(R.string.key_time));
        filters = m.findPreference(getString(R.string.key_groups));
        filtersHelp = m.findPreference(getString(R.string.key_groups_help));
        themeList = m.findPreference(getString(R.string.key_theme));
        changelog = m.findPreference(getString(R.string.key_changelog));
        bugReport = m.findPreference(getString(R.string.key_report_bug));
        courseIdentifier = m.findPreference(getString(R.string.key_course_identifier));
        courseIdentifierHelp = m.findPreference(getString(R.string.key_course_identifier_help));
    }

    private void initPreferenceLists(){
        setupThemeList();
    }

    private void initTimePicker(){
        timePicker.setOnPreferenceClickListener(this);
        setTimePickerSummaryFromPrefs();
        setTimePickerEnabled(prefs.isSkipDay());
    }

    private void initChangelog(){
        changelog.setOnPreferenceClickListener(this);
    }

    private void initBugReport(){
        bugReport.setOnPreferenceClickListener(this);
    }

    private void setTimePickerSummaryFromPrefs(){
        int prevHour = prefs.getTimeHour();
        int prevMinute = prefs.getTimeMinute();
        timePicker.setSummary(new Time24Hour(prevHour == ConstantsKt.INVALID_HOUR ? 20 : prevHour, prevMinute == ConstantsKt.INVALID_MINUTE ? 0 : prevMinute).toString());
    }

    private void setTimePickerEnabled(boolean isEnabled){
        timePicker.setEnabled(isEnabled);
    }

    private void setGroupsPreferenceEnabled(boolean isEnabled){
        filters.setEnabled(isEnabled);
        filtersHelp.setEnabled(isEnabled);
    }

    private void initFilters(){
        filters.setOnBindEditTextListener(editText -> editText.setHint(getString(R.string.hint_group_highlight)));
        filtersHelp.setOnPreferenceClickListener(this);
        setGroupsSummaryFromPrefs();
        setGroupsPreferenceEnabled(prefs.isFiltersEnabled());
    }

    private void setGroupsSummaryFromPrefs(){
        String summary = prefs.getFilters();
        if(summary == null || summary.isEmpty()) {
            summary = getString(R.string.placeholder_empty);
        }
        filters.setSummary(summary);
    }

    private void initCourseIdentifier() {
        courseIdentifier.setOnBindEditTextListener(editText -> editText.setHint(getString(R.string.hint_course_identifier)));
        setCourseIdentifierSummaryFromPrefs();
    }

    private void initCourseIdentifierHelp() {
        courseIdentifierHelp.setOnPreferenceClickListener(this);
    }

    private void setCourseIdentifierSummaryFromPrefs() {
        String summary = prefs.getCourseIdentifier();
        if(summary == null || summary.isEmpty()) {
            summary = getString(R.string.placeholder_empty);
        }
        courseIdentifier.setSummary(summary);
    }

    private void setupThemeList(){
        String theme = prefs.getTheme();
        if(theme == null) {
            theme = getStringArray(R.array.theme_options)[0];
        }
        themeList.setValue(theme);
    }

    private void setTheme(){
        String themeStr = prefs.getTheme();
        if (themeStr == null) {
            themeStr = getStringArray(R.array.theme_options)[0];
        }
        AppCompatDelegate.setDefaultNightMode(Integer.parseInt(themeStr));
    }

    private void showTimePicker(){
        int prevHour = prefs.getTimeHour();
        int prevMinute = prefs.getTimeMinute();
        Time24Hour prevTime = new Time24Hour(prevHour == ConstantsKt.INVALID_HOUR ? 20 : prevHour, prevMinute == ConstantsKt.INVALID_MINUTE ? 0 : prevMinute);

        DialogFragment f = TimePickerFragmentOld.newInstance(prevTime, (TimeSetListener) setTime -> {
            prefs.setTimeHour(setTime.getHour());
            prefs.setTimeMinute(setTime.getMinute());
            setTimePickerSummaryFromPrefs();
        });

        if(getActivity() != null){
            f.show(getActivity().getSupportFragmentManager(), ConstantsKt.DIALOG_TIME_PICKER);
        }
    }

    private void showGroupsHelp(){
        showAlertDialog(
                R.string.title_groups_help,
                R.string.content_groups_help,
                R.string.okay,
                ConstantsKt.DIALOG_FILTERS_HELP
        );
    }

    private void showCourseIdentifierHelp() {
        showAlertDialog(
                R.string.title_course_identifier_help,
                R.string.content_course_identifier_help,
                R.string.okay,
                ConstantsKt.DIALOG_COURSE_IDENTIFIER_HELP
        );
    }

    private void showChangelog(){
        showAlertDialog(
                R.string.title_whats_new,
                R.string.content_whats_new,
                R.string.dismiss_whats_new,
                ConstantsKt.DIALOG_WHATS_NEW
        );
    }

    private void sendBugReport(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, getStringArray(R.array.email_addresses));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_bug_report));
        startActivity(Intent.createChooser(intent, getString(R.string.label_email_via)));
    }

    private String[] getStringArray(int id){
        if(getActivity() != null){
            return getActivity().getResources().getStringArray(id);
        }
        return null;
    }

    private void showAlertDialog(@StringRes int title, @StringRes int content, @StringRes int dismiss, String key) {
        DialogFragment f = AlertDialogFragment.newInstance(title, content, dismiss);
        if(getActivity() != null){ f.show(getActivity().getSupportFragmentManager(), key); }
    }
}
