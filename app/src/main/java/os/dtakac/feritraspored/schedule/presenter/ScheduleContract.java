package os.dtakac.feritraspored.schedule.presenter;

public interface ScheduleContract {
    interface Presenter {
        void onClickedPrevious();
        void onClickedNext();
        void onViewResumed(int currentNightMode);
        void onRefresh();
        void onViewCreated();
        void onViewPaused();
        void onErrorReceived(int errorCode, String description, String failingUrl);
        void onPageFinished(boolean wasErrorReceived);
        void onClickedCurrent();
        void onJavascriptInjected();
        void onPageStarted();
        void onWeekNumberReceived(String weekNumberString);
    }

    interface View {
        void loadUrl(String toLoad);
        void injectJavascript(String script);
        String getLoadedUrl();
        void refreshUi();
        void reloadCurrentPage();
        void showErrorMessage(String errMessage);
        void setLoading(boolean isLoading);
        void showMessage(String message);
        void setControlsEnabled(boolean enabled);
        void setWeekNumber(String script);
        void setToolbarTitle(String title);
        void showChangelog();
    }
}
