package os.dtakac.feritraspored.common.views.timepicker;

import java.io.Serializable;

public interface TimeSetListener extends Serializable {
    void onTimeSet(Time24Hour setTime);
}
