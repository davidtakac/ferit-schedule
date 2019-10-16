package os.dtakac.feritraspored.presenter.schedule;

import android.util.Log;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import os.dtakac.feritraspored.model.repository.IRepository;
import os.dtakac.feritraspored.model.resources.ResourceManager;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.util.JavascriptUtil;
import os.dtakac.feritraspored.util.NetworkUtil;

public class SchedulePresenter implements ScheduleContract.Presenter {

    private ScheduleContract.View view;
    private IRepository repo;
    private ResourceManager resManager;
    private JavascriptUtil jsUtil;
    private NetworkUtil netUtil;

    //the week with day that needs to be displayed according to user prefs
    private LocalDate currentDay;
    //the week with day that is currently being displayed
    private LocalDate displayedDay;

    private boolean errorReceived;

    public SchedulePresenter(ScheduleContract.View view, IRepository repo, ResourceManager resManager, JavascriptUtil jsUtil, NetworkUtil netUtil) {
        this.view = view;
        this.repo = repo;
        this.resManager = resManager;
        this.jsUtil = jsUtil;
        this.netUtil = netUtil;

        this.errorReceived = false;

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

        String displayedWeekUrl = buildDisplayedWeekUrl();
        Log.d("prestag", "url: " + displayedWeekUrl);

        String loadedUrl = view.getLoadedUrl();

        boolean wereSettingsModified = repo.get(resManager.getSettingsModifiedKey(), false);

        if(wereSettingsModified || loadedUrl == null || !loadedUrl.equals(displayedWeekUrl) || errorReceived){
            view.loadUrl(displayedWeekUrl);

            //settings were applied so update the settings modified key
            repo.add(resManager.getSettingsModifiedKey(), false);
        } else {
            //the date is correct, the webview is already on the current week, there was no error,
            //so just scroll to current day
            view.injectJavascript(buildScrollToCurrentDayScript());
        }
    }

    @Override
    public void onViewResumed() {
        boolean wereSettingsModified = repo.get(resManager.getSettingsModifiedKey(), false);

        if(wereSettingsModified){
            repo.add(resManager.getSettingsModifiedKey(), false);
            view.refreshUi();
        } else {
            boolean loadOnResume = repo.get(resManager.getLoadOnResumeKey(), false);
            if (loadOnResume) {
                loadCurrentDay();
            }
        }
    }

    @Override
    public void onRefresh() {
        if(!netUtil.isDeviceOnline()){
            view.showShortToast(resManager.getCheckNetworkString());
            return;
        }

        evaluateCurrentDay();
        view.reloadCurrentPage();
    }

    @Override
    public void applyJavascript() {
        if(errorReceived){
            return;
        }

        String js = "";

        js += buildHideElementsScript();

        if(repo.get(resManager.getGroupsToggledKey(), false)) {
            js += buildHighlightGroupsScript();
        }
        if(currentDay.withDayOfWeek(DateTimeConstants.MONDAY).equals(displayedDay.withDayOfWeek(DateTimeConstants.MONDAY))) {
            js += buildScrollToCurrentDayScript();
        }

        view.injectJavascript(js);
    }

    @Override
    public void onViewCreated() {
        boolean wereSettingsModified = repo.get(resManager.getSettingsModifiedKey(), false);
        if(wereSettingsModified){
            repo.add(resManager.getSettingsModifiedKey(), false);
        }

        boolean loadOnResume = repo.get(resManager.getLoadOnResumeKey(), false);
        if(loadOnResume){
            return;
        }

        String prevDisplayedWeek = repo.get(resManager.getPrevDisplayedWeekKey(), null);

        if(prevDisplayedWeek == null){
            loadCurrentDay();
        } else {
            setDisplayedDay(LocalDate.parse(prevDisplayedWeek));
            view.loadUrl(buildDisplayedWeekUrl());
        }
    }

    @Override
    public void onViewStopped() {
        repo.add(resManager.getPrevDisplayedWeekKey(), displayedDay.toString());
    }

    @Override
    public void onErrorReceived(int errorCode, String description, String failingUrl) {
        if(!netUtil.isDeviceOnline()){
            view.showErrorMessage(resManager.getCantLoadPageString());
        } else {
            view.showErrorMessage(
                    String.format(resManager.getUnexpectedErrorString(), errorCode, description, failingUrl)
            );
        }
    }

    @Override
    public void onPageFinished(boolean wasErrorReceived) {
        errorReceived = wasErrorReceived;
        if(!errorReceived){
            applyJavascript();
        }
    }

    @Override
    public void onClickedCurrent() {
        if(!netUtil.isDeviceOnline()){
            view.showShortToast(resManager.getCheckNetworkString());
            return;
        }
        loadCurrentDay();
    }

    @Override
    public void onClickedPrevious() {
        if(!netUtil.isDeviceOnline()){
            view.showShortToast(resManager.getCheckNetworkString());
            return;
        }
        setDisplayedDay(displayedDay.minusDays(7).withDayOfWeek(DateTimeConstants.MONDAY));

        if(displayedDay.equals(currentDay.withDayOfWeek(DateTimeConstants.MONDAY))){
            evaluateCurrentDay();
            setDisplayedDay(currentDay);
        }
        view.loadUrl(buildDisplayedWeekUrl());
    }

    @Override
    public void onClickedNext() {
        if(!netUtil.isDeviceOnline()){
            view.showShortToast(resManager.getCheckNetworkString());
            return;
        }
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

    private String buildHideElementsScript() {
        String js = "";

        js = jsUtil.hideElementsScript(resManager.getIdsToHide())
                + jsUtil.hideClassesScript(resManager.getClassesToHide())
                + jsUtil.removeElementsScript(resManager.getIdsToRemove());

        return js;
    }

    private String buildScrollToCurrentDayScript() {
        return jsUtil.scrollIntoViewScript(currentDay.toString());
    }

    private String buildHighlightGroupsScript() {
        String[] filters = repo.get(resManager.getGroupsKey(), "").split(",");
        //remove trailing and leading whitespaces
        for(int i = 0; i < filters.length; i++){
            filters[i] = filters[i].trim();
        }
        return jsUtil.highlightElementsScript(filters);
    }
}
