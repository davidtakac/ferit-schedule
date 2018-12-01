package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import os.dtakac.feritraspored.App;
import os.dtakac.feritraspored.model.repository.SharedPrefsRepository;
import os.dtakac.feritraspored.presenter.schedule.ScheduleContract;
import os.dtakac.feritraspored.presenter.schedule.SchedulePresenter;
import os.dtakac.feritraspored.util.Constants;
import os.dtakac.feritraspored.R;

public class ScheduleActivity extends AppCompatActivity implements ScheduleContract.View {

    @BindView(R.id.wv_schedule)
    WebView wvSchedule;

    @BindView(R.id.srl_schedule_swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    private ScheduleContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);

        presenter = new SchedulePresenter(
                this,
                new SharedPrefsRepository(PreferenceManager.getDefaultSharedPreferences(this)),
                App.getProgrammes()
        );

        initWebView();
        initSwipeRefresh();

        presenter.loadSchedule();
    }

    @Override
    public void loadUrlOrJavascript(String toLoad){
        wvSchedule.loadUrl(toLoad);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        handleSelectedMenuItem(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private void handleSelectedMenuItem(int itemId) {
        switch (itemId){
            case R.id.item_menu_editprogyear: startOptionsActivity(); break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadSchedule();
    }

    @Override
    public void onBackPressed() {
        if (wvSchedule.canGoBack()) {
            wvSchedule.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void initWebView() {
        wvSchedule.setWebViewClient(new ScheduleClient());
        wvSchedule.getSettings().setJavaScriptEnabled(true);
    }

    private void injectJs() {
        presenter.hideElementsOtherThanSchedule();
        presenter.scrollToCurrentDay();
        presenter.highlightSelectedGroups();
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
                presenter.loadSchedule();
            }
        });
    }

    private class ScheduleClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            setLoading(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setLoading(false);
            injectJs();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.contains(Constants.BASE_SCHEDULE_URL)){
                //this is my webpage, so let the webview load the page
                return false;
            }
            //otherwise it's not displaying the schedule webpage, so load with external browser.
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }
    }
}
