package os.dtakac.feritraspored.presenter.schedule;

public interface ScheduleContract {
    interface Presenter {
        void loadCurrentDay();
        void hideElementsOtherThanSchedule();
        void scrollToCurrentDay();
        void highlightSelectedGroups();
        void loadPreviousMonday();
        void loadNextMonday();
        void changeToDarkScheduleBackground();
        void onViewResumed();
        void onSwipeRefresh();
        void applyJavascript();
    }

    interface View {
        void loadUrl(String toLoad);
        void injectJavascript(String script);
        String getLoadedUrl();
        void refreshUi();
        void reloadCurrentPage();
    }
}
