package os.dtakac.feritraspored;

public interface ScheduleContract {
    interface Presenter {

        void loadSchedule();

        void hideElementsOtherThanSchedule();

        void scrollToCurrentDay();

        void highlightSelectedGroups();
    }

    interface View {
        void loadUrlOrJavascript(String toLoad);
    }
}
