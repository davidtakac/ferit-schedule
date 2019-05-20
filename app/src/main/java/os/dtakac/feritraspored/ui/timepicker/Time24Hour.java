package os.dtakac.feritraspored.ui.timepicker;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Time24Hour implements Serializable {
    private int hour;
    private int minute;

    public Time24Hour(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @NonNull
    @Override
    public String toString() {
        String hourStr = (hour < 10 ? "0" : "") + hour;
        String minStr = (minute < 10 ? "0" : "") + minute;

        return hourStr + ":" + minStr;
    }
}
