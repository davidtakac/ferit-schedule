package os.dtakac.feritraspored.presenter.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.model.Time24Hour;
import os.dtakac.feritraspored.model.programmes.Programme;
import os.dtakac.feritraspored.model.programmes.ProgrammeType;
import os.dtakac.feritraspored.model.programmes.Programmes;
import os.dtakac.feritraspored.model.repository.IRepository;
import os.dtakac.feritraspored.model.year.Year;
import os.dtakac.feritraspored.util.Constants;

public class OptionsPresenter implements OptionsContract.Presenter {

    private OptionsContract.View view;

    private IRepository repo;

    private Programmes progs;

    private ProgrammeType checkedType;

    private boolean wasYearInitialized;

    private Time24Hour selectedTime;

    public OptionsPresenter(OptionsContract.View view, IRepository repo, Programmes progs) {
        this.view = view;
        this.repo = repo;
        this.progs = progs;
        this.checkedType = ProgrammeType.UNDERGRAD;
        this.wasYearInitialized = false;
        this.selectedTime = getPreviouslySelectedTime();
    }

    @Override
    public void initViewValues() {
        view.setCheckedRadioButton(repo.get(Constants.CHECKED_PROGTYPE_ID_KEY, R.id.rb_options_undergrad));

        view.setProgramme(repo.get(Constants.CHECKED_PROG_POS_KEY, 0));

        view.setSkipSaturdayChecked(repo.get(Constants.SKIP_SATURDAY_KEY, false));

        view.setNextDayChecked(repo.get(Constants.NEXTDAY_KEY, false));

        view.setGroupFilterText(repo.get(Constants.GROUP_FILTER_KEY, ""));

        view.setTimePickerButtonText(getPreviouslySelectedTime().toString());

        view.setTimePickerButtonEnabled(repo.get(Constants.NEXTDAY_KEY, false));
    }

    @Override
    public void setProgSpinnerData(int checkedRadiobtnId) {
        setCheckedType(checkedRadiobtnId);
        view.setProgSpinnerData(progs.getProgrammesByType(checkedType));
    }

    @Override
    public void setYearSpinnerData() {
        List<Year> years = new ArrayList<>();

        if(checkedType == ProgrammeType.PROF){
            Programme selected = view.getSelectedProgramme();
            if(selected.getId().equals("53")){
                years.add(Year.FIRST);
            } else if(selected.getId().equals("7")){
                years.add(Year.SECOND);
                years.add(Year.THIRD);
            } else {
                years = Arrays.asList(ProgrammeType.PROF.getYears());
            }
        } else {
            years = Arrays.asList(checkedType.getYears());
        }

        view.setYearSpinnerData(years);

        if(!wasYearInitialized){
            view.setYear(repo.get(Constants.CHECKED_YEAR_POS_KEY, 0));
            wasYearInitialized = true;
        }
    }

    @Override
    public void saveOptions() {
        repo.add(Constants.PROGRAMME_KEY, view.getSelectedProgramme().getId());
        repo.add(Constants.YEAR_KEY, view.getSelectedYear().getId());

        repo.add(Constants.SKIP_SATURDAY_KEY, view.getSkipSaturdayOption());
        repo.add(Constants.NEXTDAY_KEY, view.getNextDayOption());
        repo.add(Constants.GROUP_FILTER_KEY, view.getGroupFilterText());

        repo.add(Constants.CHECKED_PROGTYPE_ID_KEY, view.getCheckedRbId());
        repo.add(Constants.CHECKED_PROG_POS_KEY, view.getProgSpinnerPosition());
        repo.add(Constants.CHECKED_YEAR_POS_KEY, view.getYearSpinnerPosition());

        repo.add(Constants.SELECTED_HOUR_KEY, selectedTime.getHour());
        repo.add(Constants.SELECTED_MIN_KEY, selectedTime.getMinute());
    }

    private Time24Hour getPreviouslySelectedTime(){
        int hour = repo.get(Constants.SELECTED_HOUR_KEY, 20);
        int min = repo.get(Constants.SELECTED_MIN_KEY, 0);

        return new Time24Hour(hour,min);
    }

    @Override
    public Time24Hour getSelectedTime() {
        return selectedTime;
    }

    @Override
    public void setSelectedTime(Time24Hour selectedTime) {
        this.selectedTime = selectedTime;
        view.setTimePickerButtonText(selectedTime.toString());
    }

    private void setCheckedType(int checkedId) {
        ProgrammeType type = ProgrammeType.UNDERGRAD;

        switch(checkedId){
            case R.id.rb_options_grad: type = ProgrammeType.GRAD; break;
            case R.id.rb_options_prof: type = ProgrammeType.PROF; break;
            case R.id.rb_options_diff: type = ProgrammeType.DIFF; break;
            default: break;
        }

        checkedType = type;
    }
}
