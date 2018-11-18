package os.dtakac.feritraspored.model.programmes;

import java.util.HashMap;

// TODO: 18-Nov-18 rewrite programme classes and save their stylized names in strings.xml
// TODO: 18-Nov-18 write missing methods
// TODO: 18-Nov-18 relocate programme classes to this class
public class Programmes {

    public Programme getProgrammeById(int id){
        return Undergrad.EE;
    }

    public Programme[] getProgrammesByType(ProgrammeType type){
        return Undergrad.values();
    }
}
