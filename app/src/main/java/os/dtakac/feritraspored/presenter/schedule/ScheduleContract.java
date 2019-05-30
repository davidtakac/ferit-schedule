package os.dtakac.feritraspored.presenter.schedule;

public interface ScheduleContract {
    interface Presenter {
        void loadCurrentDay();
        void onClickedPrevious();
        void onClickedNext();
        void onViewResumed();
        void onRefresh();
        void applyJavascript();
        void onViewCreated();
        void onViewStopped();
        void onErrorReceived(int errorCode, String description, String failingUrl);
        void onPageFinished(boolean wasErrorReceived);

        void onClickedCurrent();
    }

    interface View {
        void loadUrl(String toLoad);
        void injectJavascript(String script);
        String getLoadedUrl();
        void refreshUi();
        void reloadCurrentPage();
        void showErrorMessage(String errMsg);

        void showShortToast(String message);
        void setControlsEnabled(boolean enabled);
    }
}
