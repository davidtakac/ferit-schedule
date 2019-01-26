package os.dtakac.feritraspored.presenter.schedule;

import android.util.Log;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import os.dtakac.feritraspored.model.resources.ResourceManager;
import os.dtakac.feritraspored.model.repository.IRepository;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.util.JavascriptUtil;

public class SchedulePresenter implements ScheduleContract.Presenter {

    private ScheduleContract.View view;
    private IRepository repo;
    private ResourceManager resManager;
    private JavascriptUtil jsUtil;

    //the week with day that needs to be displayed according to user prefs
    private LocalDate currentDay;
    //the week with day that is currently being displayed
    private LocalDate displayedDay;

    public SchedulePresenter(ScheduleContract.View view, IRepository repo, ResourceManager resManager, JavascriptUtil jsUtil) {
        this.view = view;
        this.repo = repo;
        this.resManager = resManager;
        this.jsUtil = jsUtil;

        evaluateCurrentDay();
    }

    @Override
    public void loadCurrentDay() {
        //re-evaluate current date to display because it is possible the app was paused
        //before the time after which the display should be shifted to next day and
        //resumed after it. not including this line here would result in the app not
        //shifting to the next day correctly.
        evaluateCurrentDay();

        //after evaluating current week, set it as the week to display
        setDisplayedDay(currentDay);

        //build the URL that should be shown
        String displayedWeekUrl = buildDisplayedWeekUrl();

        //get the currently shown URL from the webview
        String loadedUrl = view.getLoadedUrl();

        boolean wereSettingsModified = repo.get(resManager.getSettingsModifiedKey(), false);

        if(wereSettingsModified || loadedUrl == null || !loadedUrl.equals(displayedWeekUrl)){
            //if the settings were modified or the currently loaded URL doesn't equal the one
            //that is supposed to be showing, make the view load the correct url
            view.loadUrl(displayedWeekUrl);

            //settings were applied so update the settings modified key
            repo.add(resManager.getSettingsModifiedKey(), false);
        } else {
            //the date is correct and the webview is already on the current week,
            //so just scroll to current day.
            scrollToCurrentDay();
        }
    }

    @Override
    public void onViewResumed() {
        boolean wasThemeChanged = repo.get(resManager.getThemeChangedKey(), false);

        if(wasThemeChanged){
            repo.add(resManager.getThemeChangedKey(), false);
            view.refreshUi();
        } else {
            boolean loadOnResume = repo.get(resManager.getLoadOnResumeKey(), false);
            boolean settingsModified = repo.get(resManager.getSettingsModifiedKey(), false);
            if (loadOnResume || settingsModified) {
                loadCurrentDay();
            }
        }
    }

    @Override
    public void onSwipeRefresh() {
        evaluateCurrentDay();
        view.reloadCurrentPage();
    }

    @Override
    public void applyJavascript() {
        hideElementsOtherThanSchedule();
        if(repo.get(resManager.getDarkScheduleKey(), false)) {
            changeToDarkScheduleBackground();
        }
        highlightSelectedGroups();
        scrollToCurrentDay();
    }

    @Override
    public void loadPreviousMonday() {
        setDisplayedDay(displayedDay.minusDays(7).withDayOfWeek(DateTimeConstants.MONDAY));

        if(displayedDay.equals(currentDay.withDayOfWeek(DateTimeConstants.MONDAY))){
            evaluateCurrentDay();
            setDisplayedDay(currentDay);
        }
        view.loadUrl(buildDisplayedWeekUrl());
    }

    @Override
    public void loadNextMonday() {
        setDisplayedDay(displayedDay.plusDays(7).withDayOfWeek(DateTimeConstants.MONDAY));

        if(displayedDay.equals(currentDay.withDayOfWeek(DateTimeConstants.MONDAY))){
            evaluateCurrentDay();
            setDisplayedDay(currentDay);
        }
        view.loadUrl(buildDisplayedWeekUrl());
    }

    private void evaluateCurrentDay() {
        LocalDate date = new LocalDate();
        LocalTime time = new LocalTime();

        boolean skipSaturday = repo.get(resManager.getSkipSaturdayKey(), false);
        boolean skipToNextDay = repo.get(resManager.getSkipDayKey(), false);

        if(skipToNextDay) {
            date = addDayIfTimeGreaterThanPrefs(date, time);
        }

        if(date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            date = date.plusDays(1);
        } else if(skipSaturday && date.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            date = date.plusDays(2);
        }

        currentDay = date;
    }

    private LocalDate addDayIfTimeGreaterThanPrefs(LocalDate date, LocalTime time){
        int hour = repo.get(resManager.getHourKey(), 20);
        int minute = repo.get(resManager.getMinuteKey(), 0);

        return date.plusDays(
                (time.getHourOfDay() >= hour && time.getMinuteOfHour() >= minute) ? 1 : 0
        );
    }

    private String buildDisplayedWeekUrl() {
        String defaultProgId = resManager.getUndergradProgrammeId(0);
        String defaultYearId = resManager.getUndergradYearId(0);

        return  resManager.getScheduleUrl()
                //current week
                + displayedDay.withDayOfWeek(DateTimeConstants.MONDAY).toString()
                //selected year
                + "/" + repo.get(resManager.getYearKey(), defaultYearId)
                //selected programme
                + "-" + repo.get(resManager.getProgrammeKey(), defaultProgId);
    }

    private void setDisplayedDay(LocalDate week){
        displayedDay = week;
    }

    @Override
    public void hideElementsOtherThanSchedule() {
        view.injectJavascript(jsUtil.hideElementsScript(resManager.getIdsToHide()));
        view.injectJavascript(jsUtil.hideClassesScript(resManager.getClassesToHide()));
        view.injectJavascript(jsUtil.removeElementsScript(resManager.getIdsToRemove()));
    }

    @Override
    public void changeToDarkScheduleBackground() {
        view.injectJavascript(jsUtil.invertElementsColor(resManager.getIdsToInvertColor(), "0.925"));
        view.injectJavascript(jsUtil.invertClassesColor(resManager.getClassesToInvertColor(), "1"));
        view.injectJavascript(jsUtil.changeClassesBackground(resManager.getClassesToSetBackground(), resManager.getClassBackgrounds()));
    }


    @Override
    public void scrollToCurrentDay() {
        if(currentDay.withDayOfWeek(DateTimeConstants.MONDAY).equals(displayedDay.withDayOfWeek(DateTimeConstants.MONDAY))) {
            view.injectJavascript(jsUtil.scrollIntoViewScript(currentDay.toString()));
        }
    }

    @Override
    public void highlightSelectedGroups() {
        String[] filters = repo.get(resManager.getGroupsKey(), "").split(",");
        view.injectJavascript(jsUtil.highlightElementsScript(filters));
    }
}
