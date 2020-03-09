package os.dtakac.feritraspored.model.resources;

public interface ResourceManager {

    //preference keys
    String getSkipSaturdayKey();
    String getSkipDayKey();
    String getHourKey();
    String getMinuteKey();
    String getProgrammeKey();
    String getYearKey();
    String getGroupsKey();
    String getSettingsModifiedKey();
    String getLoadOnResumeKey();
    String getPrevDisplayedWeekKey();
    String getGroupsToggledKey();

    String getScheduleUrl();

    //url-ready programme ids
    String getUndergradProgrammeId(int index);
    String getUndergradYearId(int index);

    //user interface strings
    String getCheckNetworkString();
    String getCantLoadPageString();
    String getUnexpectedErrorString();
}
