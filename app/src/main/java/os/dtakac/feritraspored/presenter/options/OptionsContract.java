package os.dtakac.feritraspored.presenter.options;

import java.util.List;

import os.dtakac.feritraspored.model.programmes.Programme;
import os.dtakac.feritraspored.model.year.Year;

public interface OptionsContract {
    interface View{
        void checkRadioButton(int radioButtonId);
        void selectYear(int position);
        void selectProgramme(int position);
        void checkSkipSaturdayOption(boolean isChecked);
        void checkNextDayAfter8pmOption(boolean isChecked);
        void setGroupFilterText(String text);

        void setProgSpinnerData(List<Programme> data);
        void setYearSpinnerData(List<Year> data);

        Programme getSelectedProgramme();
        Year getSelectedYear();
        boolean getSkipSaturdayOption();
        boolean getNextDayAfter8pmOption();
        String getGroupFilterText();

        int getCheckedRbId();
        int getProgSpinnerPosition();
        int getYearSpinnerPosition();
    }

    interface Presenter{

        void initViewValues();
        void setProgSpinnerData(int checkedRadiobtnId);
        void setYearSpinnerData();
        void saveOptions();
    }
}
