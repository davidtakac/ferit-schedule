package os.dtakac.feritraspored.ui.schedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import os.dtakac.feritraspored.model.resources.ResourceManager;
import os.dtakac.feritraspored.ui.listener.DebouncedOnClickListener;
import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.model.repository.SharedPrefsRepository;
import os.dtakac.feritraspored.model.resources.AndroidResourceManager;
import os.dtakac.feritraspored.presenter.schedule.ScheduleContract;
import os.dtakac.feritraspored.presenter.schedule.SchedulePresenter;
import os.dtakac.feritraspored.ui.settings.SettingsActivity;
import os.dtakac.feritraspored.util.JavascriptUtil;
import os.dtakac.feritraspored.util.NetworkUtil;

public class ScheduleActivity extends AppCompatActivity implements ScheduleContract.View {

    @BindView(R.id.wv_schedule)
    WebView wvSchedule;

    //navigation buttons
    @BindView(R.id.btn_navbar_current)
    ImageButton btnCurrent;

    @BindView(R.id.btn_navbar_next)
    ImageButton btnNext;

    @BindView(R.id.btn_navbar_previous)
    ImageButton btnPrevious;

    //status views
    @BindView(R.id.cl_schedule_status)
    ConstraintLayout clStatus;

    @BindView(R.id.pbar_schedule_status)
    ProgressBar pbarStatus;

    @BindView(R.id.iv_schedule_error_status)
    ImageView ivError;

    @BindView(R.id.tv_schedule_status)
    TextView tvStatus;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ScheduleContract.Presenter presenter;

    //in millis
    private long debounceThreshold = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);

        ResourceManager rm = new AndroidResourceManager(getResources());
        presenter = new SchedulePresenter(
                this,
                new SharedPrefsRepository(PreferenceManager.getDefaultSharedPreferences(this)),
                rm,
                new JavascriptUtil(getAssets(), rm),
                new NetworkUtil(this)
        );

        initToolbar();
        initWebView();
        initNavbar();

        presenter.onViewCreated();
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
            new Handler().postDelayed(() -> setLoading(false), 100);
        });
    }

    @Override
    public void setWeekNumber(String script){
        wvSchedule.evaluateJavascript(script, s -> {
            String noQuotations = s.replace("\"", "");
            String title = noQuotations;

            if(noQuotations.isEmpty() || noQuotations.equals("null") || noQuotations.equals("undefined")){
                title = getString(R.string.schedule_label);
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
        tvStatus.setText("");
        clStatus.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showErrorMessage(String message){
        ivError.setVisibility(View.VISIBLE);
        pbarStatus.setVisibility(View.INVISIBLE);
        tvStatus.setText(message);
        clStatus.setVisibility(View.VISIBLE);
    }

    @Override
    public void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void openUrlInExternalBrowser(String url){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void openUrlInCustomTabs(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        //gets primary color
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

    private void setToolbarTitle(String title){
        toolbar.setTitle(title);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        wvSchedule.setWebViewClient(new ScheduleClient());
        wvSchedule.getSettings().setJavaScriptEnabled(true);
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
