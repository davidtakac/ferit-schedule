package os.dtakac.feritraspored.views.timepicker;

import java.io.Serializable;

public interface TimeSetListener extends Serializable {
    void onTimeSet(Time24Hour setTime);
}
