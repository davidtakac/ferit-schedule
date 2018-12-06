package os.dtakac.feritraspored.ui.TimePicker;

import java.io.Serializable;

import os.dtakac.feritraspored.model.Time24Hour;

public interface TimeSetListener extends Serializable {
    void onTimeSet(Time24Hour setTime);
}
