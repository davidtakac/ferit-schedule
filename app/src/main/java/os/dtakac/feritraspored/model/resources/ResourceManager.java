package os.dtakac.feritraspored.model.resources;

public interface ResourceManager {

    //url-ready programme ids
    String getUndergradProgrammeId(int index);
    String getUndergradYearId(int index);

    //file paths
    String getHighlightScriptPath();
}