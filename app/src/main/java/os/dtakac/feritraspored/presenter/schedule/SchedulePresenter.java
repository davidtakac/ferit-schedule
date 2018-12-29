package os.dtakac.feritraspored.presenter.schedule;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import os.dtakac.feritraspored.model.resources.ResourceManager;
import os.dtakac.feritraspored.model.repository.IRepository;
import os.dtakac.feritraspored.util.JavascriptUtil;

public class SchedulePresenter implements ScheduleContract.Presenter {

    private static String[] idsToHide = {"header-top","header","gototopdiv","footer","sidebar","napomene"};
    private static String[] classesToHide = {"naslov-kategorije","odabir"};
    private static String[] idsToRemove = {"izbor-studija"};
    private static String[] idsToInvertColor = {"content-contain"};
    private static String[] classesToInvertColor = {"thumbnail"};

    private static String[] classesToSetBackground = {"blokovi LV", "blokovi KV", "blokovi PR", "blokovi AV", "blokovi IS"};
    private static String[] classBackgrounds = {"#002636", "#000149", "#1E0520", "#322100", "#002A7F"};

    private ScheduleContract.View view;
    private IRepository repo;
    private ResourceManager resManager;
    private JavascriptUtil jsUtil;

    private LocalDate currentWeek;
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
        String loadedUrl = view.getLoadedUrl();
        setDisplayedWeek(currentWeek);

        boolean settingsModified = repo.get(resManager.getSettingsModifiedKey(), false);

        if(settingsModified || loadedUrl == null || !loadedUrl.equals(buildDisplayedWeekUrl())){
            //re-evaluate the date to display and load the URL again
            evaluateCurrentWeek();
            setDisplayedWeek(currentWeek);
            view.loadUrl(buildDisplayedWeekUrl());

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
    public void changeToDarkBackground() {
        view.injectJavascript(jsUtil.invertElementsColor(idsToInvertColor, "0.925"));
        view.injectJavascript(jsUtil.invertClassesColor(classesToInvertColor, "1"));
        view.injectJavascript(jsUtil.changeClassesBackground(classesToSetBackground, classBackgrounds));
    }

    @Override
    public void onResume() {
        boolean themeChanged = repo.get(resManager.getThemeChangedKey(), false);

        if(themeChanged){
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
    public void applyJavascript() {
        if(repo.get(resManager.getDarkThemeKey(), false)) {
            changeToDarkBackground();
        }
        hideElementsOtherThanSchedule();
        scrollToCurrentDay();
        highlightSelectedGroups();
    }

    @Override
    public void scrollToCurrentDay() {
        view.injectJavascript(jsUtil.scrollIntoViewScript(displayedWeek.toString()));
    }

    @Override
    public void highlightSelectedGroups() {
        String[] filters = repo.get(resManager.getGroupsKey(), "").split(",");
        view.injectJavascript(jsUtil.highlightElementsScript(filters));
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
            date = skipToNextDay(date, time);
        }

        if(date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            date = date.plusDays(1);
        } else if(skipSaturday) {
            date = skipSaturday(date);
        }

        currentWeek = date;
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

    private String buildDisplayedWeekUrl() {
        String defaultProgId = resManager.getUndergradProgrammeId(0);
        String defaultYearId = resManager.getUndergradYearId(0);

        return  resManager.getScheduleUrl()
                //load current week
                + displayedWeek.withDayOfWeek(DateTimeConstants.MONDAY).toString()
                //load selected year
                + "/" + repo.get(resManager.getYearKey(), defaultYearId)
                //load selected programme
                + "-" + repo.get(resManager.getProgrammeKey(), defaultProgId)
                ;
    }

    private void setDisplayedWeek(LocalDate week){
        displayedWeek = week;
    }
}
