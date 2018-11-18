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

import butterknife.BindView;
import butterknife.ButterKnife;
import os.dtakac.feritraspored.model.programmes.Undergrad;
import os.dtakac.feritraspored.model.programmes.Year;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.util.SharedPrefsUtil;

// TODO: 17-Nov-18 add fab to skip to next week
public class ScheduleActivity extends AppCompatActivity {

    @BindView(R.id.wv_schedule)
    WebView wvSchedule;

    @BindView(R.id.srl_schedule_swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //if the app was started for the first time, let the user pick his programme and year
        if(!SharedPrefsUtil.get(this, Constants.PREVIOUSLY_STARTED, false)){
            SharedPrefsUtil.save(this, Constants.PREVIOUSLY_STARTED, true);

            startOptionsActivity();
            finish();
        }

        ButterKnife.bind(this);

        initWebView();
        initSwipeRefresh();
        loadSchedule();
    }

    private void loadSchedule() {
        wvSchedule.loadUrl(getUrl());
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
        wvSchedule.loadUrl(getUrl());
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
                injectJsHighlighting();
            }
        });

        //enables logging in to aai edu in one click.
        wvSchedule.getSettings().setJavaScriptEnabled(true);
    }

    private void injectJsHighlighting() {
        // TODO: 16-Nov-18 separate parts of this js code for easier upgradability
        wvSchedule.loadUrl("javascript:($(\"p:contains('4/24'),p:contains('PR-2'),p:contains('4/16')\").css(\"text-transform\",\"uppercase\").css(\"color\",\"#EF271B\"))");

        //wvSchedule.loadUrl("javascript:(" + buildJsBody() + ")");
    }

    private String getUrl() {
        LocalDate date = new LocalDate();
        String url = Constants.BASE_FERIT_URL + Constants.BASE_SCHEDULE_URL;

        if(date.getDayOfWeek() == 7) {
            //if now is on a sunday, set now to be next monday and display next week's schedule
            date = date.plusDays(1);
        }

        return  url
                + date.withDayOfWeek(DateTimeConstants.MONDAY).toString()
                + "/" + SharedPrefsUtil.get(this, Constants.YEAR_KEY, Undergrad.EE.getId())
                + "-" + SharedPrefsUtil.get(this, Constants.PROGRAMME_KEY, Year.FIRST.getId())

                //scroll to current day of the week
                + "#" + date.toString()
                ;
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
