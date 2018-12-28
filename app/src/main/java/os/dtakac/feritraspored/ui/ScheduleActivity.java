package os.dtakac.feritraspored.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import os.dtakac.feritraspored.model.resources.AndroidResourceManager;
import os.dtakac.feritraspored.model.repository.SharedPrefsRepository;
import os.dtakac.feritraspored.presenter.schedule.ScheduleContract;
import os.dtakac.feritraspored.presenter.schedule.SchedulePresenter;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.ui.settings.SettingsActivity;
import os.dtakac.feritraspored.util.JavascriptUtil;

public class ScheduleActivity extends AppCompatActivity implements ScheduleContract.View {

    @BindView(R.id.wv_schedule)
    WebView wvSchedule;

    @BindView(R.id.srl_schedule_swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.navbar_schedule_navigation)
    BottomNavigationViewEx navbar;

    private ScheduleContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);

        presenter = new SchedulePresenter(
                this,
                new SharedPrefsRepository(PreferenceManager.getDefaultSharedPreferences(this)),
                new AndroidResourceManager(getResources()),
                new JavascriptUtil(getAssets())
        );

        initActionBar();
        initWebView();
        initSwipeRefresh();
        initNavbar();

        loadCurrentDay();
    }

    private void initActionBar() {
        setTitle(getString(R.string.schedule_label));
    }

    private void loadCurrentDay(){
        presenter.loadCurrentWeek();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        boolean loadOnResume = m.getBoolean(getString(R.string.prefkey_loadonresume), false);

        if(loadOnResume) {
            loadCurrentDay();
        }
    }

    @Override
    public void loadUrl(String url){
        wvSchedule.loadUrl(url);
    }

    @Override
    public void injectJavascript(String script){
        wvSchedule.evaluateJavascript(script, value -> {
            //nothing yet
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
            case R.id.item_menu_openinbrowser:
                openUrlInExternalBrowser(wvSchedule.getUrl());
                break;
            default:
                break;
        }
    }

    private void initWebView() {
        wvSchedule.setWebViewClient(new ScheduleClient());
        wvSchedule.getSettings().setJavaScriptEnabled(true);
    }

    private void initNavbar(){
        navbar.enableAnimation(false);
    }

    @OnClick({R.id.item_navitems_current, R.id.item_navitems_next, R.id.item_navitems_previous})
    void navItemClicked(View v){
        animateButtonPress(v);
        switch(v.getId()){
            case R.id.item_navitems_current: loadCurrentDay(); break;
            case R.id.item_navitems_previous: presenter.loadPreviousWeek(); break;
            case R.id.item_navitems_next: presenter.loadNextWeek(); break;
            default: break;
        }
    }

    private void animateButtonPress(View v){
        AlphaAnimation a = new AlphaAnimation(0.5f, 1.0f);
        a.setDuration(300);
        a.setFillAfter(true);
        v.startAnimation(a);
    }

    private void setLoading(boolean isLoading){
        swipeRefresh.setRefreshing(isLoading);
    }

    private void initSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> wvSchedule.reload());
    }

    private void openUrlInExternalBrowser(String url){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private class ScheduleClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Snackbar s = Snackbar.make(ScheduleActivity.this.findViewById(R.id.constraintlayout_scheduleactivity),
                    R.string.schedule_openurlinbrowser,
                    Snackbar.LENGTH_LONG
            );
            s.setAction(R.string.schedule_actionopen, v -> openUrlInExternalBrowser(url));
            s.show();

            return true;
        }

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

    private void applyJavascript() {
        presenter.hideElementsOtherThanSchedule();
        presenter.scrollToCurrentDay();
        presenter.highlightSelectedGroups();
    }
}
