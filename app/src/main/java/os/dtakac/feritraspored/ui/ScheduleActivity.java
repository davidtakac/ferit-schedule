package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import os.dtakac.feritraspored.model.resources.AndroidResourceManager;
import os.dtakac.feritraspored.model.repository.SharedPrefsRepository;
import os.dtakac.feritraspored.presenter.schedule.ScheduleContract;
import os.dtakac.feritraspored.presenter.schedule.SchedulePresenter;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.ui.settings.SettingsActivity;
import os.dtakac.feritraspored.util.Constants;

// TODO: 12/9/18 after the user exits settings activity, reload this activity(or apply settings)
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
                new AndroidResourceManager(getResources())
        );

        initWebView();
        initSwipeRefresh();

        presenter.loadCurrentDay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadCurrentDay();
    }

    @Override
    public void loadUrl(String url){
        wvSchedule.loadUrl(url);
    }

    @Override
    public void injectJavascript(String script){
        wvSchedule.evaluateJavascript(script, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //nothing yet
            }
        });
    }

    @Override
    public String getLoadedUrl() {
        return wvSchedule.getUrl();
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
            case R.id.item_menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            default:
                break;
        }
    }

    @OnClick(R.id.fab_schedule_loadschedule)
    void onFabClick(){
        presenter.loadCurrentDay();
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

    private void applyJavascript() {
        presenter.hideElementsOtherThanSchedule();
        presenter.scrollToCurrentDay();
        presenter.highlightSelectedGroups();
    }

    private void setLoading(boolean isLoading){
        swipeRefresh.setRefreshing(isLoading);
    }

    private void initSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                wvSchedule.reload();
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
            if(url.contains(getString(R.string.ferit_scheduleurl))) {
                applyJavascript();
            }
        }
    }
}
