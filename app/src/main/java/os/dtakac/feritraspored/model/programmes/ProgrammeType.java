package os.dtakac.feritraspored.model.programmes;

public enum ProgrammeType {
    UNDERGRAD("Preddiplomski", 3), GRAD("Diplomski", 2), PROF("Stručni", 3), DIFF("Razlikovna", 1);

    private final String name;
    private final int yearAmount;

    ProgrammeType(String name, int yearAmount) {
        this.name = name;
        this.yearAmount = yearAmount;
    }

    public Year[] getYears(){
        Year[] years = new Year[yearAmount];
        for(int i = 0; i < yearAmount; i++){
            years[i] = Year.values()[i];
        }
        return years;
    }

    public String getName() {
        return name;
    }
}
