package os.dtakac.feritraspored.model.programmes;

import android.content.res.AssetManager;

import java.io.IOException;
import java.util.List;

import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.util.FileUtil;

public class Programmes {

    private List<Programme> undergrad;
    private List<Programme> graduate;
    private List<Programme> professional;
    private List<Programme> differential;

    public Programmes(AssetManager am){
        try {
            undergrad = FileUtil.parseListOfProgrammes(am.open("programmes/undergrad.txt"));
            graduate = FileUtil.parseListOfProgrammes(am.open("programmes/graduate.txt"));
            professional = FileUtil.parseListOfProgrammes(am.open("programmes/professional.txt"));
            differential = FileUtil.parseListOfProgrammes(am.open("programmes/differential.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Programme> getProgrammesByType(ProgrammeType type){
        List<Programme> result = undergrad;
        switch (type){
            case GRAD: result = graduate; break;
            case PROF: result = professional; break;
            case DIFF: result = differential; break;
            default: break;
        }
        return result;
    }
}
