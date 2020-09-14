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
import os.dtakac.feritraspored.common.PrefsRepository;
import os.dtakac.feritraspored.common.ResourceManager;
import os.dtakac.feritraspored.common.util.Constants;
import os.dtakac.feritraspored.common.util.JavascriptUtil;
import os.dtakac.feritraspored.common.util.NetworkUtil;

public class SchedulePresenter implements ScheduleContract.Presenter {

    private ScheduleContract.View view;
    private PrefsRepository prefs;
    private ResourceManager res;
    private JavascriptUtil jsUtil;
    private NetworkUtil netUtil;

    private LocalDate currentDay, displayedDay;
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
    public void onViewResumed(int currentNightMode) {
        this.currentNightMode = currentNightMode;
        if(wereSettingsModified()){
            prefs.add(res.get(R.string.prefkey_settings_modified), false);
            view.refreshUi();
        } else {
            boolean loadOnResume = prefs.get(res.get(R.string.prefkey_load_on_resume), false);
            if (loadOnResume) { loadCurrentDay(); }
        }
        if(prefs.get(Constants.LAST_VERSION_KEY, -1) < BuildConfig.VERSION_CODE){
            prefs.add(Constants.LAST_VERSION_KEY, BuildConfig.VERSION_CODE);
            view.showChangelog();
        }
    }

    @Override
    public void onRefresh() {
        if(!netUtil.isDeviceOnline()){
            view.showMessage(res.get(R.string.notify_no_network));
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
            title = res.get(R.string.label_schedule);
        }
        view.setToolbarTitle(title);
    }

    @Override
    public void onViewCreated() {
        if(wereSettingsModified()){
            prefs.add(res.get(R.string.prefkey_settings_modified), false);
        }
        boolean loadOnResume = prefs.get(res.get(R.string.prefkey_load_on_resume), false);
        if(loadOnResume){ return; }

        String prevDisplayedWeek = prefs.get(res.get(R.string.prefkey_previously_displayed_week), null);
        if(prevDisplayedWeek == null){
            loadCurrentDay();
        } else {
            displayedDay = LocalDate.parse(prevDisplayedWeek);
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
            // the controls will be enabled when javascript is applied
            applyJavascript();
        } else {
            // if there was an error, enable controls so the user can spam them
            view.setControlsEnabled(true);
        }
    }

    @Override
    public void onClickedCurrent() {
        if(!netUtil.isDeviceOnline()){
            view.showMessage(res.get(R.string.notify_no_network));
            return;
        }
        view.setControlsEnabled(false);
        loadCurrentDay();
    }

    @Override
    public void onClickedPrevious() {
        if(!netUtil.isDeviceOnline()){
            view.showMessage(res.get(R.string.notify_no_network));
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
        if(!netUtil.isDeviceOnline()){
            view.showMessage(res.get(R.string.notify_no_network));
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

        if(wereSettingsModified() || loadedUrl == null || !loadedUrl.equals(displayedWeekUrl) || errorReceived){
            //not on current week's page, load it
            view.loadUrl(displayedWeekUrl);
            prefs.add(res.get(R.string.prefkey_settings_modified), false);
        } else {
            //already on current week's page, just scroll to current day
            view.injectJavascript(buildScrollToCurrentDayScript());
        }
    }

    private void applyJavascript() {
        if(errorReceived){ return; }
        view.setWeekNumber(jsUtil.weekNumberScript());

        String js = jsUtil.hideAllButScheduleScript() + jsUtil.timeOnBlocksScript();
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
                + displayedDay.withDayOfWeek(DateTimeConstants.MONDAY).toString() //date
                + "/" + prefs.get(res.get(R.string.prefkey_year), defaultYearId) //year
                + "-" + prefs.get(res.get(R.string.prefkey_programme), defaultProgId); //programme
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

    private boolean wereSettingsModified(){
        return prefs.get(res.get(R.string.prefkey_settings_modified), false);
    }
}
