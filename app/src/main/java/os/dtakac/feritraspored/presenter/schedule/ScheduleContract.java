package os.dtakac.feritraspored.presenter.schedule;

public interface ScheduleContract {
    interface Presenter {
        void loadCurrentWeekScrollToCurrentDay();
        void hideElementsOtherThanSchedule();
        void scrollToCurrentDay();
        void highlightSelectedGroups();
        void loadPreviousWeek();
        void loadNextWeek();
        void changeToDarkBackground();
    }

    interface View {
        void loadUrl(String toLoad);
        void injectJavascript(String script);
        String getLoadedUrl();
    }
}
