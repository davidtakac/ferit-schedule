package os.dtakac.feritraspored.presenter.schedule;

import android.util.Log;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import os.dtakac.feritraspored.model.resources.ResourceManager;
import os.dtakac.feritraspored.model.repository.IRepository;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.util.JavascriptUtil;

// TODO: 12/12/18 implement forward/backward buttons
public class SchedulePresenter implements ScheduleContract.Presenter {

    private static String[] idsToHide = {"header-top","header","gototopdiv","footer","sidebar","napomene"};
    private static String[] classesToHide = {"naslov-kategorije"};
    private static String[] idsToRemove = {"izbor-studija"};

    private ScheduleContract.View view;
    private IRepository repo;
    private ResourceManager resManager;
    private JavascriptUtil jsUtil;

    private LocalDate dateToDisplay;

    public SchedulePresenter(ScheduleContract.View view, IRepository repo, ResourceManager resManager, JavascriptUtil jsUtil) {
        this.view = view;
        this.repo = repo;
        this.resManager = resManager;
        this.jsUtil = jsUtil;

        updateDateToDisplay();
    }

    @Override
    public void loadCurrentDay() {
        String loadedUrl = view.getLoadedUrl();

        boolean settingsModified = repo.get(resManager.getSettingsModifiedKey(), false);
        boolean isDateOutdated = !dateToDisplay.toString().equals(repo.get(resManager.getLastDisplayedDateKey(), ""));

        if(settingsModified || isDateOutdated || loadedUrl == null || !loadedUrl.equals(buildScheduleUrl())){
            //re-evaluate the date to display and load the URL again

            updateDateToDisplay();
            view.loadUrl(buildScheduleUrl());

            //settings were applied so update the settings modified key
            repo.add(resManager.getSettingsModifiedKey(), false);
        } else {
            //the date is correct and the webview is already on the current week,
            //so just scroll to current day.

            scrollToCurrentDay();
        }
    }

    @Override
    public void hideElementsOtherThanSchedule() {
        view.injectJavascript(jsUtil.hideElementsScript(idsToHide));
        view.injectJavascript(jsUtil.removeElementsScript(idsToRemove));
        view.injectJavascript(jsUtil.hideClassesScript(classesToHide));
    }

    @Override
    public void scrollToCurrentDay() {
        view.injectJavascript(jsUtil.scrollIntoViewScript(dateToDisplay.toString()));
    }

    @Override
    public void highlightSelectedGroups() {
        String[] filters = repo.get(resManager.getGroupsKey(), "").split(",");
        view.injectJavascript(jsUtil.highlightElementsScript(filters));
    }

    private void updateDateToDisplay() {
        LocalDate date = new LocalDate();
        LocalTime time = new LocalTime();

        boolean skipSaturday = repo.get(resManager.getSkipSaturdayKey(), false);
        boolean skipToNextDay = repo.get(resManager.getSkipDayKey(), false);

        if(skipToNextDay) {
            date = skipToNextDay(date, time);
        }

        if(date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            date = date.plusDays(1);
        } else if(skipSaturday) {
            date = skipSaturday(date);
        }

        dateToDisplay = date;
        repo.add(resManager.getLastDisplayedDateKey(), dateToDisplay.toString());
    }

    private LocalDate skipToNextDay(LocalDate date, LocalTime time){
        int hour = repo.get(resManager.getHourKey(), 20);
        int minute = repo.get(resManager.getMinuteKey(), 0);

        return date.plusDays(
                (time.getHourOfDay() >= hour && time.getMinuteOfHour() >= minute) ? 1 : 0
        );
    }

    private LocalDate skipSaturday(LocalDate date){
        return date.plusDays((date.getDayOfWeek() == DateTimeConstants.SATURDAY) ? 2 : 0);
    }

    private String buildScheduleUrl() {
        String defaultProgId = resManager.getUndergradProgrammeId(0);
        String defaultYearId = resManager.getUndergradYearId(0);

        return  resManager.getScheduleUrl()
                //load current week
                + dateToDisplay.withDayOfWeek(DateTimeConstants.MONDAY).toString()
                //load selected year
                + "/" + repo.get(resManager.getYearKey(), defaultYearId)
                //load selected programme
                + "-" + repo.get(resManager.getProgrammeKey(), defaultProgId)
                ;
    }
}
