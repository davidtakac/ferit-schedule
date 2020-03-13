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
        return getStr(R.string.prefkey_skip_saturday);
    }

    @Override
    public String getSkipDayKey() {
        return getStr(R.string.prefkey_skip_day);
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
    public String getLoadOnResumeKey() {
        return getStr(R.string.prefkey_load_on_resume);
    }

    @Override
    public String getPrevDisplayedWeekKey() {
        return getStr(R.string.prefkey_previously_displayed_week);
    }

    @Override
    public String getGroupsToggledKey() {
        return getStr(R.string.prefkey_groups_toggle);
    }

    @Override
    public String getScheduleUrl() {
        return getStr(R.string.base_url) + getStr(R.string.schedule_url);
    }

    @Override
    public String getUndergradProgrammeId(int index) {
        String[] undergrad = getStrArray(R.array.values_undergrad);
        if(index <= undergrad.length - 1){
            return getStrArray(R.array.values_undergrad)[index];
        }
        return null;
    }

    @Override
    public String getUndergradYearId(int index) {
        String[] undergrad = getStrArray(R.array.values_years_undergrad);
        if(index <= undergrad.length - 1){
            return getStrArray(R.array.values_years_undergrad)[index];
        }
        return null;
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

    @Override
    public String getHighlightScriptPath() {
        return getStr(R.string.highlight_script_path);
    }

    private String getStr(int id){
        return r.getString(id);
    }

    private String[] getStrArray(int id){
        return r.getStringArray(id);
    }
}
