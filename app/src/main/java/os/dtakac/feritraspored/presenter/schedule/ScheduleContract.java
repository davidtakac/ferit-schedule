package os.dtakac.feritraspored.presenter.schedule;

public interface ScheduleContract {
    interface Presenter {
        void loadSchedule();
        void hideElementsOtherThanSchedule();
        void scrollToCurrentDay();
        void highlightSelectedGroups();
    }

    interface View {
        void loadUrl(String toLoad);
        void injectJavascript(String script);
    }
}
