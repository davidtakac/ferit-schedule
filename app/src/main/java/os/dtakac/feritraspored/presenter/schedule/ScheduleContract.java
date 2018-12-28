package os.dtakac.feritraspored.presenter.schedule;

public interface ScheduleContract {
    interface Presenter {
        void loadCurrentWeek();
        void hideElementsOtherThanSchedule();
        void scrollToCurrentDay();
        void highlightSelectedGroups();
        void loadPreviousWeek();
        void loadNextWeek();
    }

    interface View {
        void loadUrl(String toLoad);
        void injectJavascript(String script);
        String getLoadedUrl();
    }
}
