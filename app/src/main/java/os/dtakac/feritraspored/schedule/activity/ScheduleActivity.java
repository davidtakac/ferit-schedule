package os.dtakac.feritraspored.schedule.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import os.dtakac.feritraspored.BuildConfig;
import os.dtakac.feritraspored.common.listener.DebouncedOnClickListener;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.common.PrefsRepository;
import os.dtakac.feritraspored.common.ResourceManager;
import os.dtakac.feritraspored.common.util.Constants;
import os.dtakac.feritraspored.common.views.groups.AlertDialogFragment;
import os.dtakac.feritraspored.schedule.presenter.ScheduleContract;
import os.dtakac.feritraspored.schedule.presenter.SchedulePresenter;
import os.dtakac.feritraspored.settings.activity.SettingsActivity;
import os.dtakac.feritraspored.common.util.JavascriptUtil;
import os.dtakac.feritraspored.common.util.NetworkUtil;

public class ScheduleActivity extends AppCompatActivity implements ScheduleContract.View {

    @BindView(R.id.wv_schedule) WebView wvSchedule;
    @BindView(R.id.toolbar) Toolbar toolbar;

    //navigation buttons
    @BindView(R.id.btn_navbar_current) ImageButton btnCurrent;
    @BindView(R.id.btn_navbar_next) ImageButton btnNext;
    @BindView(R.id.btn_navbar_previous) ImageButton btnPrevious;
    //status views
    @BindView(R.id.cl_schedule_status) ConstraintLayout clStatus;
    @BindView(R.id.pbar_schedule_status) ProgressBar pbarStatus;
    @BindView(R.id.iv_schedule_error_status) ImageView ivError;
    @BindView(R.id.tv_schedule_status) TextView tvStatus;
    @BindView(R.id.btn_schedule_bug_report) Button btnBugReport;

    private ScheduleContract.Presenter presenter;
    private ResourceManager rm;

    //in millis
    private long debounceThreshold = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);
        initPresenter();
        initViews();
        presenter.onViewCreated();
        showChangelog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onViewResumed(getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK);
    }

    @Override
    protected void onPause() {
        presenter.onViewPaused();
        super.onPause();
    }

    private void initViews(){
        initToolbar();
        initWebView();
        initNavbar();
    }

    private void initPresenter(){
        rm = new ResourceManager(getResources());
        presenter = new SchedulePresenter(
                this,
                new PrefsRepository(PreferenceManager.getDefaultSharedPreferences(this)),
                rm,
                new JavascriptUtil(getAssets(), rm),
                new NetworkUtil(this)
        );
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.menu);
        toolbar.getMenu().findItem(R.id.item_menu_refresh).setOnMenuItemClickListener(item -> {
            presenter.onRefresh();
            return true;
        });
        toolbar.getMenu().findItem(R.id.item_menu_settings).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(ScheduleActivity.this, SettingsActivity.class));
            return true;
        });
        toolbar.getMenu().findItem(R.id.item_menu_openinbrowser).setOnMenuItemClickListener(item -> {
            openUrlInExternalBrowser(getLoadedUrl());
            return true;
        });
    }

    @Override
    public void loadUrl(String url){
        wvSchedule.loadUrl(url);
    }

    @Override
    public void injectJavascript(String script){
        wvSchedule.evaluateJavascript(script, s -> {
            setControlsEnabled(true);
            //delayed loading turn off so the webview has time to update
            new Handler().postDelayed(() -> setLoading(false), 200);
        });
    }

    @Override
    public void setWeekNumber(String script){
        wvSchedule.evaluateJavascript(script, s -> {
            String noQuotations = s.replace("\"", "");
            String title = noQuotations;

            if(noQuotations.isEmpty() || noQuotations.equals("null") || noQuotations.equals("undefined")){
                title = getString(R.string.label_schedule);
            }

            setToolbarTitle(title);
        });
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

    @Override
    public void setControlsEnabled(boolean enabled) {
        btnNext.setEnabled(enabled);
        btnPrevious.setEnabled(enabled);
        btnCurrent.setEnabled(enabled);
    }

    private void setLoading(boolean loading){
        ivError.setVisibility(View.GONE);
        pbarStatus.setVisibility(View.VISIBLE);
        btnBugReport.setVisibility(View.GONE);
        tvStatus.setText(null);
        clStatus.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showErrorMessage(String message){
        ivError.setVisibility(View.VISIBLE);
        pbarStatus.setVisibility(View.INVISIBLE);
        btnBugReport.setVisibility(View.VISIBLE);
        btnBugReport.setOnClickListener((v) -> sendBugReport(message));
        tvStatus.setText(message);
        clStatus.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void openUrlInExternalBrowser(String url){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void openUrlInCustomTabs(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(rm.getColor(R.color.gray900));

        //launches url in custom tab
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(ScheduleActivity.this, Uri.parse(url));
    }

    private void setToolbarTitle(String title){
        toolbar.setTitle(title);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        wvSchedule.setWebViewClient(new ScheduleClient());
        wvSchedule.getSettings().setJavaScriptEnabled(true);
    }

    private void sendBugReport(String content){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, rm.getArray(R.array.email_addresses));
        intent.putExtra(Intent.EXTRA_SUBJECT, rm.get(R.string.subject_bug_report));
        intent.putExtra(Intent.EXTRA_TEXT, String.format(rm.get(R.string.template_bug_report), content));
        startActivity(Intent.createChooser(intent, rm.get(R.string.label_email_via)));
    }

    private void showChangelog(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastSavedVersionCode = prefs.getInt(Constants.VERSION_KEY, -1);
        if(lastSavedVersionCode < BuildConfig.VERSION_CODE){
            AlertDialogFragment.newInstance(R.string.title_whats_new, R.string.content_whats_new, R.string.dismiss_whats_new)
                    .show(getSupportFragmentManager(), Constants.WHATS_NEW_KEY);
            prefs.edit().putInt(Constants.VERSION_KEY, BuildConfig.VERSION_CODE).apply();
        }
    }

    private void initNavbar(){
        btnNext.setOnClickListener(new DebouncedOnClickListener(debounceThreshold) {
            @Override
            public void onDebouncedClick() {
                presenter.onClickedNext();
            }
        });
        btnCurrent.setOnClickListener(new DebouncedOnClickListener(debounceThreshold) {
            @Override
            public void onDebouncedClick() {
                presenter.onClickedCurrent();
            }
        });
        btnPrevious.setOnClickListener(new DebouncedOnClickListener(debounceThreshold) {
            @Override
            public void onDebouncedClick() {
                presenter.onClickedPrevious();
            }
        });
    }

    private class ScheduleClient extends WebViewClient {

        private boolean errorReceived = false;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            openUrlInCustomTabs(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            setLoading(true);
            setControlsEnabled(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            errorReceived = true;
            presenter.onErrorReceived(errorCode, description, failingUrl);

            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            presenter.onPageFinished(errorReceived);
            if(errorReceived){
                //if there was an error, enable the controls so the user can spam them or something
                setControlsEnabled(true);
            }
            errorReceived = false;
        }
    }
}
