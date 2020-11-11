package os.dtakac.feritraspored.schedule.presenter;

import android.content.res.Configuration;
import android.os.Handler;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import os.dtakac.feritraspored.BuildConfig;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.common.ResourceManager;
import os.dtakac.feritraspored.common.constants.ConstantsKt;
import os.dtakac.feritraspored.common.preferences.PreferenceRepository;
import os.dtakac.feritraspored.common.scripts.ScriptProvider;

public class SchedulePresenter implements ScheduleContract.Presenter {

    private ScheduleContract.View view;
    private PreferenceRepository prefs;
    private ResourceManager res;
    private ScriptProvider scriptProvider;

    private LocalDate currentDay, displayedDay;
    private boolean errorReceived;
    private int currentNightMode = Configuration.UI_MODE_NIGHT_NO;

    public SchedulePresenter(ScheduleContract.View view, PreferenceRepository prefs, ResourceManager resManager, ScriptProvider scriptProvider) {
        this.view = view;
        this.prefs = prefs;
        this.res = resManager;
        this.scriptProvider = scriptProvider;
        this.errorReceived = false;
        evaluateCurrentDay();
    }

    @Override
    public void onViewResumed(int currentNightMode) {
        this.currentNightMode = currentNightMode;
        if(prefs.isSettingsModified()) {
            prefs.setSettingsModified(false);
            view.refreshUi();
        } else {
            if (prefs.isLoadOnResume()) { loadCurrentDay(); }
        }
        if(prefs.getVersion() < BuildConfig.VERSION_CODE){
            prefs.setVersion(BuildConfig.VERSION_CODE);
            view.showChangelog();
        }
    }

    @Override
    public void onRefresh() {
        if(!view.isOnline()){
            view.showMessage(res.getString(R.string.notify_no_network));
            return;
        }
        evaluateCurrentDay();
        view.setControlsEnabled(false);
        view.reloadCurrentPage();
    }

    @Override
    public void onWeekNumberReceived(String weekNumberString) {
        String noQuotations = weekNumberString.replace("\"", "");
        String title = noQuotations;
        if(noQuotations.isEmpty() || noQuotations.equals("null") || noQuotations.equals("undefined")){
            title = res.getString(R.string.label_schedule);
        }
        view.setToolbarTitle(title);
    }

    @Override
    public void onViewCreated() {
        if(prefs.isSettingsModified()){
            prefs.setSettingsModified(false);
        }
        if(prefs.isLoadOnResume()){ return; }

        String prevDisplayedWeek = prefs.getPreviouslyDisplayedWeek();
        if(prevDisplayedWeek == null){
            loadCurrentDay();
        } else {
            displayedDay = LocalDate.parse(prevDisplayedWeek);
            view.loadUrl(buildDisplayedWeekUrl());
        }
    }

    @Override
    public void onViewPaused() {
        prefs.setPreviouslyDisplayedWeek(displayedDay.toString());
    }

    @Override
    public void onErrorReceived(int errorCode, String description, String failingUrl) {
        if(!view.isOnline()){
            view.showErrorMessage(res.getString(R.string.notify_cant_load_page));
        } else {
            String errMsg = String.format(res.getString(R.string.notify_unexpected_error), errorCode, description, failingUrl);
            view.showErrorMessage(errMsg);
        }
    }

    @Override
    public void onPageFinished(boolean wasErrorReceived) {
        errorReceived = wasErrorReceived;
        if(!errorReceived){
            // the controls will be enabled when javascript is applied
            applyJavascript();
        } else {
            // if there was an error, enable controls so the user can spam them
            view.setControlsEnabled(true);
        }
    }

    @Override
    public void onClickedCurrent() {
        if(!view.isOnline()){
            view.showMessage(res.getString(R.string.notify_no_network));
            return;
        }
        view.setControlsEnabled(false);
        loadCurrentDay();
    }

    @Override
    public void onClickedPrevious() {
        if(!view.isOnline()){
            view.showMessage(res.getString(R.string.notify_no_network));
            return;
        }
        view.setControlsEnabled(false);
        displayedDay = displayedDay.minusDays(7).withDayOfWeek(DateTimeConstants.MONDAY);

        if(displayedDay.equals(currentDay.withDayOfWeek(DateTimeConstants.MONDAY))){
            evaluateCurrentDay();
            displayedDay = currentDay;
        }
        view.loadUrl(buildDisplayedWeekUrl());
    }

    @Override
    public void onJavascriptInjected() {
        view.setControlsEnabled(true);
        // delayed loading turn off so the webview has time to apply js
        new Handler().postDelayed(() -> view.setLoading(false), 200);
    }

    @Override
    public void onPageStarted() {
        view.setLoading(true);
        view.setControlsEnabled(false);
    }

    @Override
    public void onClickedNext() {
        if(!view.isOnline()){
            view.showMessage(res.getString(R.string.notify_no_network));
            return;
        }
        view.setControlsEnabled(false);
        displayedDay = displayedDay.plusDays(7).withDayOfWeek(DateTimeConstants.MONDAY);

        if(displayedDay.equals(currentDay.withDayOfWeek(DateTimeConstants.MONDAY))){
            evaluateCurrentDay();
            displayedDay = currentDay;
        }
        view.loadUrl(buildDisplayedWeekUrl());
    }

    private void loadCurrentDay() {
        evaluateCurrentDay();
        displayedDay = currentDay;
        String displayedWeekUrl = buildDisplayedWeekUrl();
        String loadedUrl = view.getLoadedUrl();

        if(prefs.isSettingsModified() || loadedUrl == null || !loadedUrl.equals(displayedWeekUrl) || errorReceived){
            //not on current week's page, load it
            view.loadUrl(displayedWeekUrl);
            prefs.setSettingsModified(false);
        } else {
            //already on current week's page, just scroll to current day
            view.injectJavascript(buildScrollToCurrentDayScript());
        }
    }

    private void applyJavascript() {
        if(errorReceived){ return; }
        view.setWeekNumber(scriptProvider.getWeekNumberFunction());

        String js = scriptProvider.hideJunkFunction() + scriptProvider.timeOnBlocksFunction();
        if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            js += scriptProvider.darkThemeFunction();
        }
        if(prefs.isFiltersEnabled()) {
            js += buildHighlightGroupsScript();
        }
        if(currentDay.withDayOfWeek(DateTimeConstants.MONDAY).equals(displayedDay.withDayOfWeek(DateTimeConstants.MONDAY))) {
            js += buildScrollToCurrentDayScript();
        }
        view.injectJavascript(js);
    }

    private void evaluateCurrentDay() {
        LocalDate date = new LocalDate();
        LocalTime time = new LocalTime();

        if(prefs.isSkipDay()) {
            date = addDayIfTimeGreaterThanPrefs(date, time);
        }

        if(date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            date = date.plusDays(1);
        } else if(prefs.isSkipSaturday() && date.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            date = date.plusDays(2);
        }
        currentDay = date;
    }

    private LocalDate addDayIfTimeGreaterThanPrefs(LocalDate date, LocalTime time){
        int hour = prefs.getTimeHour() == ConstantsKt.INVALID_HOUR ? 20 : prefs.getTimeHour();
        int minute = prefs.getTimeMinute() == ConstantsKt.INVALID_MINUTE ? 0 : prefs.getTimeMinute();

        return date.plusDays(
                (time.getHourOfDay() >= hour && time.getMinuteOfHour() >= minute) ? 1 : 0
        );
    }

    private String buildDisplayedWeekUrl() {
        return  res.getString(R.string.base_url) + res.getString(R.string.schedule_url)
                + displayedDay.withDayOfWeek(DateTimeConstants.MONDAY).toString()
                + "/" + (prefs.getCourseIdentifier() == null ? "" : prefs.getCourseIdentifier());
    }

    private String buildScrollToCurrentDayScript() {
        // get short day name in croatian (pon, fri, sri..)
        DateTimeFormatter formatter = DateTimeFormat.forPattern("E");
        String dayCroatian = formatter.withLocale(new Locale("hr", "HR")).print(currentDay);

        // capitalize first letter to fit format used in url
        String firstLetter = dayCroatian.substring(0,1);
        firstLetter = firstLetter.equals("č") ? "C" : firstLetter.toUpperCase();
        dayCroatian = firstLetter + dayCroatian.substring(1);

        // scroll schedule to display current day
        return scriptProvider.scrollIntoViewFunction(dayCroatian);
    }

    private String buildHighlightGroupsScript() {
        String filtersFromPrefs = prefs.getFilters() == null ? "" : prefs.getFilters();
        String[] filters = filtersFromPrefs.split(",");
        //remove trailing and leading whitespaces
        for(int i = 0; i < filters.length; i++){
            filters[i] = filters[i].trim();
        }
        return scriptProvider.highlightBlocksFunction(filters);
    }
}
