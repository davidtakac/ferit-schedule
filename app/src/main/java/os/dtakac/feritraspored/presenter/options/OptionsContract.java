package os.dtakac.feritraspored.presenter.options;

import java.util.List;

import os.dtakac.feritraspored.model.Time24Hour;
import os.dtakac.feritraspored.model.programmes.Programme;
import os.dtakac.feritraspored.model.year.Year;

public interface OptionsContract {
    interface View{
        void setCheckedRadioButton(int radioButtonId);
        void setYear(int position);
        void setProgramme(int position);
        void setSkipSaturdayChecked(boolean isChecked);
        void setNextDayChecked(boolean isChecked);
        void setGroupFilterText(String text);
        void setProgSpinnerData(List<Programme> data);
        void setYearSpinnerData(List<Year> data);
        void setTimePickerButtonText(String time);
        void setTimePickerButtonEnabled(boolean isEnabled);

        Programme getSelectedProgramme();
        Year getSelectedYear();
        boolean getSkipSaturdayOption();
        boolean getNextDayOption();
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
        Time24Hour getSelectedTime();
        void setSelectedTime(Time24Hour selectedTime);
    }
}
