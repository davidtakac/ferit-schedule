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
    String getThemeChangedKey();
    String getLoadOnResumeKey();
    String getDarkScheduleKey();
    String getPrevDisplayedWeekKey();
    String getGroupsToggledKey();

    String getScheduleUrl();

    //url-ready programme ids
    String getUndergradProgrammeId(int index);
    String getUndergradYearId(int index);

    //string arrays for modifying schedule webpage
    String[] getIdsToHide();
    String[] getClassesToHide();
    String[] getIdsToRemove();
    String[] getIdsToInvertColor();
    String[] getClassesToInvertColor();
    String[] getClassesToSetBackground();
    String[] getClassBackgrounds();

    //user interface strings
    String getCheckNetworkString();
    String getCantLoadPageString();
    String getUnexpectedErrorString();
}
