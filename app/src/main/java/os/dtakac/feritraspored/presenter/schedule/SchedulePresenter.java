package os.dtakac.feritraspored.presenter.schedule;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import os.dtakac.feritraspored.model.resources.ResourceManager;
import os.dtakac.feritraspored.model.repository.IRepository;
import os.dtakac.feritraspored.util.JavascriptUtil;

public class SchedulePresenter implements ScheduleContract.Presenter {

    //names of ids and classes to hide in order to simplify website
    private static final String[] idsToHide = {"header-top","header","gototopdiv","footer","sidebar","napomene"};
    private static final String[] classesToHide = {"naslov-kategorije","odabir"};
    private static final String[] idsToRemove = {"izbor-studija"};

    //ids and class names needed to apply dark schedule theme
    private static final String[] idsToInvertColor = {"content-contain"};
    private static final String[] classesToInvertColor = {"thumbnail"};
    private static final String[] classesToSetBackground = {"blokovi LV", "blokovi KV", "blokovi PR", "blokovi AV", "blokovi IS"};
    //class backgrounds are inverted colors of the original backgrounds
    private static final String[] classBackgrounds = {"#002636", "#000149", "#1E0520", "#322100", "#002A7F"};

    private ScheduleContract.View view;
    private IRepository repo;
    private ResourceManager resManager;
    private JavascriptUtil jsUtil;

    //the week with day that needs to be displayed according to user prefs
    private LocalDate currentWeek;
    //the week with day that is currently being displayed
    private LocalDate displayedWeek;

    public SchedulePresenter(ScheduleContract.View view, IRepository repo, ResourceManager resManager, JavascriptUtil jsUtil) {
        this.view = view;
        this.repo = repo;
        this.resManager = resManager;
        this.jsUtil = jsUtil;

        evaluateCurrentWeek();
    }

    @Override
    public void loadCurrentWeekOrScrollToDay() {
        //re-evaluate current date to display because it is possible the app was paused
        //before the time after which the display should be shifted to next day and
        //resumed after it. not including this line here would result in the app not
        //shifting to the next day correctly.
        evaluateCurrentWeek();

        //after evaluating current week, set it as the week to display
        setDisplayedWeek(currentWeek);

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
    public void onResume() {
        boolean wasThemeChanged = repo.get(resManager.getThemeChangedKey(), false);

        if(wasThemeChanged){
            repo.add(resManager.getThemeChangedKey(), false);
            view.refreshUi();
        } else {
            boolean loadOnResume = repo.get(resManager.getLoadOnResumeKey(), false);
            boolean settingsModified = repo.get(resManager.getSettingsModifiedKey(), false);
            if (loadOnResume || settingsModified) {
                loadCurrentWeekOrScrollToDay();
            }
        }
    }

    @Override
    public void onReload() {
        evaluateCurrentWeek();
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
    public void loadPreviousWeek() {
        setDisplayedWeek(displayedWeek.minusDays(7).withDayOfWeek(DateTimeConstants.MONDAY));

        if(displayedWeek.equals(currentWeek.withDayOfWeek(DateTimeConstants.MONDAY))){
            evaluateCurrentWeek();
            setDisplayedWeek(currentWeek);
        }
        view.loadUrl(buildDisplayedWeekUrl());
    }

    @Override
    public void loadNextWeek() {
        setDisplayedWeek(displayedWeek.plusDays(7).withDayOfWeek(DateTimeConstants.MONDAY));

        if(displayedWeek.equals(currentWeek.withDayOfWeek(DateTimeConstants.MONDAY))){
            evaluateCurrentWeek();
            setDisplayedWeek(currentWeek);
        }
        view.loadUrl(buildDisplayedWeekUrl());
    }

    private void evaluateCurrentWeek() {
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

        currentWeek = date;
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
                //load current week
                + displayedWeek.withDayOfWeek(DateTimeConstants.MONDAY).toString()
                //load selected year
                + "/" + repo.get(resManager.getYearKey(), defaultYearId)
                //load selected programme
                + "-" + repo.get(resManager.getProgrammeKey(), defaultProgId);
    }

    private void setDisplayedWeek(LocalDate week){
        displayedWeek = week;
    }

    @Override
    public void hideElementsOtherThanSchedule() {
        view.injectJavascript(jsUtil.hideElementsScript(idsToHide));
        view.injectJavascript(jsUtil.hideClassesScript(classesToHide));
        view.injectJavascript(jsUtil.removeElementsScript(idsToRemove));
    }

    @Override
    public void changeToDarkScheduleBackground() {
        view.injectJavascript(jsUtil.invertElementsColor(idsToInvertColor, "0.925"));
        view.injectJavascript(jsUtil.invertClassesColor(classesToInvertColor, "1"));
        view.injectJavascript(jsUtil.changeClassesBackground(classesToSetBackground, classBackgrounds));
    }


    @Override
    public void scrollToCurrentDay() {
        view.injectJavascript(jsUtil.scrollIntoViewScript(currentWeek.toString()));
    }

    @Override
    public void highlightSelectedGroups() {
        String[] filters = repo.get(resManager.getGroupsKey(), "").split(",");
        view.injectJavascript(jsUtil.highlightElementsScript(filters));
    }
}
