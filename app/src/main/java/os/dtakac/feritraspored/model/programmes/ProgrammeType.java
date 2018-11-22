package os.dtakac.feritraspored.model.programmes;

import os.dtakac.feritraspored.model.year.Year;

public enum ProgrammeType {
    UNDERGRAD(3), GRAD(2), PROF(3), DIFF(1);

    private final int yearAmount;

    ProgrammeType(int yearAmount) {
        this.yearAmount = yearAmount;
    }

    public Year[] getYears(){
        Year[] years = new Year[yearAmount];
        for(int i = 0; i < yearAmount; i++){
            years[i] = Year.values()[i];
        }
        return years;
    }
}
