package os.dtakac.feritraspored.model.resources;

import android.content.res.Resources;

import os.dtakac.feritraspored.R;

public class AndroidResourceManager implements ResourceManager {

    Resources r;

    public AndroidResourceManager(Resources r) {
        this.r = r;
    }

    @Override
    public String getSkipSaturdayKey() {
        return r.getString(R.string.prefkey_skipsaturday);
    }

    @Override
    public String getSkipDayKey() {
        return r.getString(R.string.prefkey_skipday);
    }

    @Override
    public String getHourKey() {
        return r.getString(R.string.prefkey_time_hour);
    }

    @Override
    public String getMinuteKey() {
        return r.getString(R.string.prefkey_time_minute);
    }

    @Override
    public String getProgrammeKey() {
        return r.getString(R.string.prefkey_programme);
    }

    @Override
    public String getYearKey() {
        return r.getString(R.string.prefkey_year);
    }

    @Override
    public String getGroupsKey() {
        return r.getString(R.string.prefkey_groups);
    }

    @Override
    public String getSettingsModifiedKey() {
        return r.getString(R.string.prefkey_settings_modified);
    }

    @Override
    public String getDarkThemeKey() {
        return r.getString(R.string.prefkey_darktheme);
    }

    @Override
    public String getThemeChangedKey() {
        return r.getString(R.string.prefkey_themechanged);
    }

    @Override
    public String getLoadOnResumeKey() {
        return r.getString(R.string.prefkey_loadonresume);
    }

    @Override
    public String getDarkScheduleKey() {
        return r.getString(R.string.prefkey_darkschedule);
    }

    @Override
    public String getScheduleUrl() {
        return r.getString(R.string.ferit_baseurl) + r.getString(R.string.ferit_scheduleurl);
    }

    @Override
    public String getUndergradProgrammeId(int index) {
        String[] undergrad = r.getStringArray(R.array.undergrad_values);
        if(index <= undergrad.length - 1){
            return r.getStringArray(R.array.undergrad_values)[index];
        }
        return null;
    }

    @Override
    public String getUndergradYearId(int index) {
        String[] undergrad = r.getStringArray(R.array.year_undergrad_values);
        if(index <= undergrad.length - 1){
            return r.getStringArray(R.array.year_undergrad_values)[index];
        }
        return null;
    }
}
