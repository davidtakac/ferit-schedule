package os.dtakac.feritraspored.ui.schedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.model.repository.SharedPrefsRepository;
import os.dtakac.feritraspored.model.resources.AndroidResourceManager;
import os.dtakac.feritraspored.presenter.schedule.ScheduleContract;
import os.dtakac.feritraspored.presenter.schedule.SchedulePresenter;
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
        setTheme();
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

        presenter.onViewCreated();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onViewResumed();
    }

    @Override
    protected void onStop() {
        presenter.onViewStopped();
        super.onStop();
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

    @Override
    public void loadUrl(String url){
        wvSchedule.loadUrl(url);
    }

    @Override
    public void injectJavascript(String script){
        wvSchedule.evaluateJavascript(script, null);
    }

    @Override
    public String getLoadedUrl() {
        return wvSchedule.getUrl();
    }

    @Override
    public void refreshUi() {
        recreate();
    }

    @Override
    public void reloadCurrentPage() {
        wvSchedule.reload();
    }

    private void loadCurrentDay(){
        presenter.loadCurrentDay();
    }

    private void handleSelectedMenuItem(int itemId) {
        switch (itemId){
            case R.id.item_menu_settings: {
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            }
            case R.id.item_menu_openinbrowser: {
                openUrlInExternalBrowser(getLoadedUrl());
                break;
            }
            default: break;
        }
    }

    @OnClick({R.id.item_navitems_current, R.id.item_navitems_next, R.id.item_navitems_previous})
    void navItemClicked(View v){
        switch(v.getId()){
            case R.id.item_navitems_current: {
                loadCurrentDay();
                break;
            }
            case R.id.item_navitems_previous:{
                presenter.loadPreviousMonday();
                break;
            }
            case R.id.item_navitems_next: {
                presenter.loadNextMonday();
                break;
            }
            default: break;
        }
    }

    private void initActionBar() {
        setTitle(getString(R.string.schedule_label));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        wvSchedule.setWebViewClient(new ScheduleClient());
        wvSchedule.getSettings().setJavaScriptEnabled(true);
    }

    private void initNavbar(){
        navbar.enableAnimation(false);
    }

    private void initSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> presenter.onSwipeRefresh());
    }

    private void setLoading(boolean isLoading){
        swipeRefresh.setRefreshing(isLoading);
    }

    private void setTheme(){
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkTheme = s.getBoolean(getString(R.string.prefkey_darktheme), false);

        setTheme(darkTheme ? R.style.DarkTheme : R.style.LightTheme);
    }

    private void openUrlInExternalBrowser(String url){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void openUrlInCustomTabs(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        //gets primary color of current theme
        int appCompatAttribute = this.getResources().getIdentifier("colorPrimary", "attr", this.getPackageName());
        TypedValue value = new TypedValue();
        this.getTheme().resolveAttribute (appCompatAttribute, value, true);
        @ColorInt int colorPrimary = value.data;

        //sets toolbar color to match app
        builder.setToolbarColor(colorPrimary);

        //launches url in custom tab
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(ScheduleActivity.this, Uri.parse(url));
    }

    private class ScheduleClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            openUrlInCustomTabs(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            setLoading(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setLoading(false);
            presenter.applyJavascript();
        }
    }

}
