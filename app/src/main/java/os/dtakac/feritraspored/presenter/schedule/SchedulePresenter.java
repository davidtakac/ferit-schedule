package os.dtakac.feritraspored.presenter.schedule;

import android.util.Log;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import os.dtakac.feritraspored.model.programmes.ProgrammeType;
import os.dtakac.feritraspored.model.programmes.Programmes;
import os.dtakac.feritraspored.model.repository.IRepository;
import os.dtakac.feritraspored.model.year.Year;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.util.JsUtil;

public class SchedulePresenter implements ScheduleContract.Presenter {

    private ScheduleContract.View view;

    private IRepository repo;

    private Programmes progs;

    private LocalDate dateToDisplay;

    public SchedulePresenter(ScheduleContract.View view, IRepository repo, Programmes progs) {
        this.view = view;
        this.repo = repo;
        this.progs = progs;

        this.dateToDisplay = generateDateBasedOnUserPrefs();
    }

    @Override
    public void loadCurrentDay() {
        String currentWeekUrl = buildScheduleUrl();
        String loadedUrl = view.getLoadedUrl();

        if(loadedUrl == null || !loadedUrl.equals(currentWeekUrl)){
            view.loadUrl(currentWeekUrl);
        } else {
            scrollToCurrentDay();
        }
    }

    @Override
    public void hideElementsOtherThanSchedule() {
        view.injectJavascript(createHideElementsJsFunction());
        view.injectJavascript(createRemoveElementsJsFunction());
    }

    @Override
    public void scrollToCurrentDay() {
        view.injectJavascript(createScrollIntoViewJsFunction());
    }

    @Override
    public void highlightSelectedGroups() {
        view.injectJavascript(createElementHighlightJsFunction());
    }

    private LocalDate generateDateBasedOnUserPrefs() {
        LocalDate date = new LocalDate();
        LocalTime time = new LocalTime();

        boolean skipSaturday = repo.get(Constants.SKIP_SATURDAY_KEY, false);
        boolean nextDayAfter8pm = repo.get(Constants.NEXTDAY_AFTER_8PM_KEY, false);

        if(nextDayAfter8pm) {
            date = skipToNextDayAfter8pm(date, time);
        }

        if(date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            date = date.plusDays(1);
        } else if(skipSaturday) {
            date = skipSaturday(date);
        }

        return date;
    }

    private LocalDate skipToNextDayAfter8pm(LocalDate date, LocalTime time){
        return date.plusDays((time.getHourOfDay() >= 20) ? 1 : 0);
    }

    private LocalDate skipSaturday(LocalDate date){
        return date.plusDays((date.getDayOfWeek() == DateTimeConstants.SATURDAY) ? 2 : 0);
    }

    private String buildScheduleUrl() {
        String url = Constants.BASE_FERIT_URL + Constants.BASE_SCHEDULE_URL;

        String defaultProgId = progs.getProgrammesByType(ProgrammeType.UNDERGRAD).get(0).getId();
        String defaultYearId = Year.FIRST.getId();

        return  url
                //load current week
                + dateToDisplay.withDayOfWeek(DateTimeConstants.MONDAY).toString()
                //load selected year
                + "/" + repo.get(Constants.YEAR_KEY, defaultYearId)
                //load selected programme
                + "-" + repo.get(Constants.PROGRAMME_KEY, defaultProgId)
                ;
    }

    private String createHideElementsJsFunction() {
        return "(" +
                JsUtil.toHideElementsWithIdFunction("header-top,header,gototopdiv,footer,sidebar") +
                "())";
    }

    private String createRemoveElementsJsFunction() {
        return "(" +
                JsUtil.toRemoveElementsWithIdFunction("izbor-studija") +
                "())";
    }

    private String createScrollIntoViewJsFunction() {
        return  "(" +
                JsUtil.toScrollIntoViewFunction(dateToDisplay.toString()) +
                "())";
    }

    private String createElementHighlightJsFunction() {
        String pContainsQuery = JsUtil.toPContains(
                repo.get(Constants.GROUP_FILTER_KEY, "")
        );

        return "($(\"" +
                pContainsQuery +
                "\").css(\"text-transform\",\"uppercase\").css(\"color\",\"#EF271B\"))";
    }
}
