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
        return getStr(R.string.prefkey_skipsaturday);
    }

    @Override
    public String getSkipDayKey() {
        return getStr(R.string.prefkey_skipday);
    }

    @Override
    public String getHourKey() {
        return getStr(R.string.prefkey_time_hour);
    }

    @Override
    public String getMinuteKey() {
        return getStr(R.string.prefkey_time_minute);
    }

    @Override
    public String getProgrammeKey() {
        return getStr(R.string.prefkey_programme);
    }

    @Override
    public String getYearKey() {
        return getStr(R.string.prefkey_year);
    }

    @Override
    public String getGroupsKey() {
        return getStr(R.string.prefkey_groups);
    }

    @Override
    public String getSettingsModifiedKey() {
        return getStr(R.string.prefkey_settings_modified);
    }

    @Override
    public String getThemeChangedKey() {
        return getStr(R.string.prefkey_themechanged);
    }

    @Override
    public String getLoadOnResumeKey() {
        return getStr(R.string.prefkey_loadonresume);
    }

    @Override
    public String getDarkScheduleKey() {
        return getStr(R.string.prefkey_darkschedule);
    }

    @Override
    public String getPrevDisplayedWeekKey() {
        return getStr(R.string.prefkey_prevdisplayedweek);
    }

    @Override
    public String getGroupsToggledKey() {
        return getStr(R.string.prefkey_groups_toggle);
    }

    @Override
    public String getScheduleUrl() {
        return getStr(R.string.ferit_baseurl) + getStr(R.string.ferit_scheduleurl);
    }

    @Override
    public String getUndergradProgrammeId(int index) {
        String[] undergrad = getStrArray(R.array.undergrad_values);
        if(index <= undergrad.length - 1){
            return getStrArray(R.array.undergrad_values)[index];
        }
        return null;
    }

    @Override
    public String getUndergradYearId(int index) {
        String[] undergrad = getStrArray(R.array.year_undergrad_values);
        if(index <= undergrad.length - 1){
            return getStrArray(R.array.year_undergrad_values)[index];
        }
        return null;
    }

    @Override
    public String[] getIdsToHide() {
        return getStrArray(R.array.idsToHide);
    }

    @Override
    public String[] getClassesToHide() {
        return getStrArray(R.array.classesToHide);
    }

    @Override
    public String[] getIdsToRemove() {
        return getStrArray(R.array.idsToRemove);
    }

    @Override
    public String[] getIdsToInvertColor() {
        return getStrArray(R.array.idsToInvertColor);
    }

    @Override
    public String[] getClassesToInvertColor() {
        return getStrArray(R.array.classesToInvertColor);
    }

    @Override
    public String[] getClassesToSetBackground() {
        return getStrArray(R.array.classesToSetBackground);
    }

    @Override
    public String[] getClassBackgrounds() {
        return getStrArray(R.array.classBackgrounds);
    }

    @Override
    public String getCheckNetworkString() {
        return getStr(R.string.notify_no_network);
    }

    @Override
    public String getCantLoadPageString() {
        return getStr(R.string.notify_cant_load_page);
    }

    @Override
    public String getUnexpectedErrorString() {
        return getStr(R.string.notify_unexpected_error);
    }

    private String getStr(int id){
        return r.getString(id);
    }

    private String[] getStrArray(int id){
        return r.getStringArray(id);
    }
}
