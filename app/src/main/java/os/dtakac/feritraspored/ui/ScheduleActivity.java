package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import os.dtakac.feritraspored.App;
import os.dtakac.feritraspored.model.programmes.ProgrammeType;
import os.dtakac.feritraspored.model.year.Year;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.util.JsUtil;
import os.dtakac.feritraspored.util.SharedPrefsUtil;

public class ScheduleActivity extends AppCompatActivity {

    @BindView(R.id.wv_schedule)
    WebView wvSchedule;

    @BindView(R.id.srl_schedule_swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);

        initWebView();
        initSwipeRefresh();
        loadSchedule();
    }

    private void loadSchedule() {
        wvSchedule.loadUrl(buildScheduleUrl());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        handleSelectedItem(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private void handleSelectedItem(int itemId) {
        switch (itemId){
            case R.id.item_menu_editprogyear: startOptionsActivity(); break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        wvSchedule.loadUrl(buildScheduleUrl());
    }

    private void initWebView() {
        wvSchedule.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                setLoading(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setLoading(false);
                if(url.contains(Constants.BASE_SCHEDULE_URL)) {
                    //only highlight groups if the schedule is being displayed.
                    injectScheduleHighlighting();
                }
            }
        });

        //enables logging in to aai edu in one click and js injection for highlighting
        wvSchedule.getSettings().setJavaScriptEnabled(true);
    }

    private void injectScheduleHighlighting() {
        String pContainsQuery = JsUtil.parseToPContains(
                SharedPrefsUtil.get(this, Constants.GROUP_FILTER_KEY, "")
        );
        wvSchedule.loadUrl("javascript:($(\"" + pContainsQuery + "\")" +
                ".css(\"text-transform\",\"uppercase\")" +
                ".css(\"color\",\"#EF271B\"))"
        );
    }

    private String buildScheduleUrl() {
        String url = Constants.BASE_FERIT_URL + Constants.BASE_SCHEDULE_URL;

        LocalDate date = createDateToDisplayByUserPrefs();

        String prevSelectedProgId = App.getProgrammes().getProgrammesByType(ProgrammeType.UNDERGRAD).get(0).getId();
        String prevSelectedYearId = Year.FIRST.getId();

        return  url
                //load current week
                + date.withDayOfWeek(DateTimeConstants.MONDAY).toString()
                //load selected year
                + "/" + SharedPrefsUtil.get(this, Constants.YEAR_KEY, prevSelectedProgId)
                //load selected programme
                + "-" + SharedPrefsUtil.get(this, Constants.PROGRAMME_KEY, prevSelectedYearId)
                //scroll to current day of the week
                + "#" + date.toString()
                ;
    }

    private LocalDate createDateToDisplayByUserPrefs() {
        LocalDate date = new LocalDate();
        LocalTime time = new LocalTime();

        boolean skipSaturday = SharedPrefsUtil.get(this, Constants.SKIP_SATURDAY_KEY, false);
        boolean nextDayAfter8pm = SharedPrefsUtil.get(this, Constants.NEXTDAY_AFTER_8PM_KEY, false);

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

    private LocalDate skipToNextDayAfter8pm(LocalDate date, LocalTime time) {
        return date.plusDays((time.getHourOfDay() >= 20) ? 1 : 0);
    }

    private LocalDate skipSaturday(LocalDate date){
        return date.plusDays((date.getDayOfWeek() == DateTimeConstants.SATURDAY) ? 2 : 0);
    }

    @Override
    public void onBackPressed() {
        if (wvSchedule.canGoBack()) {
            wvSchedule.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void startOptionsActivity(){
        startActivity(new Intent(this, OptionsActivity.class));
    }

    private void setLoading(boolean isLoading){
        swipeRefresh.setRefreshing(isLoading);
    }

    private void initSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSchedule();
            }
        });
    }
}
