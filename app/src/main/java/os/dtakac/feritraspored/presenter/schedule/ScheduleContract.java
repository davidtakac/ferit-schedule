package os.dtakac.feritraspored.presenter.schedule;

public interface ScheduleContract {
    interface Presenter {
        void loadCurrentWeekOrScrollToDay();
        void hideElementsOtherThanSchedule();
        void scrollToCurrentDay();
        void highlightSelectedGroups();
        void loadPreviousWeek();
        void loadNextWeek();
        void changeToDarkScheduleBackground();
        void onResume();
        void applyJavascript();
    }

    interface View {
        void loadUrl(String toLoad);
        void injectJavascript(String script);
        String getLoadedUrl();
        void refreshUi();
    }
}
