package os.dtakac.feritraspored.presenter.schedule;

import android.content.res.Configuration;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.common.PrefsRepository;
import os.dtakac.feritraspored.common.ResourceManager;
import os.dtakac.feritraspored.util.JavascriptUtil;
import os.dtakac.feritraspored.util.NetworkUtil;

public class SchedulePresenter implements ScheduleContract.Presenter {

    private ScheduleContract.View view;
    private PrefsRepository prefs;
    private ResourceManager res;
    private JavascriptUtil jsUtil;
    private NetworkUtil netUtil;

    //the week with day that needs to be displayed according to user prefs
    private LocalDate currentDay;
    //the week with day that is currently being displayed
    private LocalDate displayedDay;

    private boolean errorReceived;

    private int currentNightMode = Configuration.UI_MODE_NIGHT_NO;

    public SchedulePresenter(ScheduleContract.View view, PrefsRepository prefs, ResourceManager resManager, JavascriptUtil jsUtil, NetworkUtil netUtil) {
        this.view = view;
        this.prefs = prefs;
        this.res = resManager;
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
        String loadedUrl = view.getLoadedUrl();

        boolean wereSettingsModified = getSettingsModified();
        if(wereSettingsModified || loadedUrl == null || !loadedUrl.equals(displayedWeekUrl) || errorReceived){
            view.loadUrl(displayedWeekUrl);
            //settings were applied so update the settings modified key
            prefs.add(res.get(R.string.prefkey_settings_modified), false);
        } else {
            //the date is correct, the webview is already on the current week, there was no error,
            //so just scroll to current day
            view.injectJavascript(buildScrollToCurrentDayScript());
        }
    }

    @Override
    public void onViewResumed(int currentNightMode) {
        this.currentNightMode = currentNightMode;
        boolean wereSettingsModified = getSettingsModified();
        if(wereSettingsModified){
            prefs.add(res.get(R.string.prefkey_settings_modified), false);
            view.refreshUi();
        } else {
            boolean loadOnResume = prefs.get(res.get(R.string.prefkey_load_on_resume), false);
            if (loadOnResume) {
                loadCurrentDay();
            }
        }
    }

    @Override
    public void onRefresh() {
        if(!netUtil.isDeviceOnline()){
            view.showMessage(res.get(R.string.notify_no_network));
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
        view.setWeekNumber(jsUtil.weekNumberScript());

        String js = "";
        js += buildHideElementsScript();
        js += jsUtil.timeOnBlocksScript();
        if(currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            js += jsUtil.darkThemeScript();
        }
        if(prefs.get(res.get(R.string.prefkey_groups_toggle), false)) {
            js += buildHighlightGroupsScript();
        }
        if(currentDay.withDayOfWeek(DateTimeConstants.MONDAY).equals(displayedDay.withDayOfWeek(DateTimeConstants.MONDAY))) {
            js += buildScrollToCurrentDayScript();
        }
        view.injectJavascript(js);
    }

    @Override
    public void onViewCreated() {
        boolean wereSettingsModified = getSettingsModified();
        if(wereSettingsModified){
            prefs.add(res.get(R.string.prefkey_settings_modified), false);
        }
        boolean loadOnResume = prefs.get(res.get(R.string.prefkey_load_on_resume), false);
        if(loadOnResume){
            return;
        }

        String prevDisplayedWeek = prefs.get(res.get(R.string.prefkey_previously_displayed_week), null);
        if(prevDisplayedWeek == null){
            loadCurrentDay();
        } else {
            setDisplayedDay(LocalDate.parse(prevDisplayedWeek));
            view.loadUrl(buildDisplayedWeekUrl());
        }
    }

    @Override
    public void onViewPaused() {
        prefs.add(res.get(R.string.prefkey_previously_displayed_week), displayedDay.toString());
    }

    @Override
    public void onErrorReceived(int errorCode, String description, String failingUrl) {
        if(!netUtil.isDeviceOnline()){
            view.showErrorMessage(res.get(R.string.notify_cant_load_page));
        } else {
            view.showErrorMessage(
                    String.format(res.get(R.string.notify_unexpected_error), errorCode, description, failingUrl)
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
            view.showMessage(res.get(R.string.notify_no_network));
            return;
        }
        loadCurrentDay();
    }

    @Override
    public void onClickedPrevious() {
        if(!netUtil.isDeviceOnline()){
            view.showMessage(res.get(R.string.notify_no_network));
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
            view.showMessage(res.get(R.string.notify_no_network));
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

        boolean skipToNextDay = prefs.get(res.get(R.string.prefkey_skip_day), false);
        if(skipToNextDay) {
            date = addDayIfTimeGreaterThanPrefs(date, time);
        }

        boolean skipSaturday = prefs.get(res.get(R.string.prefkey_skip_saturday), false);
        if(date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            date = date.plusDays(1);
        } else if(skipSaturday && date.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            date = date.plusDays(2);
        }
        currentDay = date;
    }

    private LocalDate addDayIfTimeGreaterThanPrefs(LocalDate date, LocalTime time){
        int hour = prefs.get(res.get(R.string.prefkey_time_hour), 20);
        int minute = prefs.get(res.get(R.string.prefkey_time_minute), 0);

        return date.plusDays(
                (time.getHourOfDay() >= hour && time.getMinuteOfHour() >= minute) ? 1 : 0
        );
    }

    private String buildDisplayedWeekUrl() {
        String defaultProgId = res.getUndergradProgrammeId(0);
        String defaultYearId = res.getUndergradYearId(0);

        return  res.get(R.string.base_url) + res.get(R.string.schedule_url)
                //current week
                + displayedDay.withDayOfWeek(DateTimeConstants.MONDAY).toString()
                //selected year
                + "/" + prefs.get(res.get(R.string.prefkey_year), defaultYearId)
                //selected programme
                + "-" + prefs.get(res.get(R.string.prefkey_programme), defaultProgId);
    }

    private void setDisplayedDay(LocalDate week){
        displayedDay = week;
    }

    private String buildHideElementsScript() {
        return jsUtil.hideAllButScheduleScript();
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
        return jsUtil.scrollIntoViewScript(dayCroatian);
    }

    private String buildHighlightGroupsScript() {
        String[] filters = prefs.get(res.get(R.string.prefkey_groups), "").split(",");
        //remove trailing and leading whitespaces
        for(int i = 0; i < filters.length; i++){
            filters[i] = filters[i].trim();
        }
        return jsUtil.highlightElementsScript(filters);
    }

    private boolean getSettingsModified(){
        return prefs.get(res.get(R.string.prefkey_settings_modified), false);
    }
}
