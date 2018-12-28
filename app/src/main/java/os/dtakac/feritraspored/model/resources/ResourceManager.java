package os.dtakac.feritraspored.model.resources;

public interface ResourceManager {

    String getSkipSaturdayKey();
    String getSkipDayKey();
    String getHourKey();
    String getMinuteKey();
    String getProgrammeKey();
    String getYearKey();
    String getGroupsKey();
    String getSettingsModifiedKey();

    String getScheduleUrl();

    String getUndergradProgrammeId(int index);
    String getUndergradYearId(int index);

    String getBackgroundColorInRgb();
}
