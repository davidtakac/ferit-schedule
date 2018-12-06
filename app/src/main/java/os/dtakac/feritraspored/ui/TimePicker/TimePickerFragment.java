package os.dtakac.feritraspored.ui.TimePicker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import os.dtakac.feritraspored.model.Time24Hour;
import os.dtakac.feritraspored.util.Constants;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static TimePickerFragment newInstance(Time24Hour initialTime, TimeSetListener listener) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.TIMEPICK_LISTENER_KEY, listener);
        args.putSerializable(Constants.TIMEPICK_TIME_KEY, initialTime);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        Time24Hour initialTime;

        if(args == null){
            initialTime = new Time24Hour(20,0);
        } else {
            initialTime = (Time24Hour) args.getSerializable(Constants.TIMEPICK_TIME_KEY);
        }

        return new TimePickerDialog(
                getActivity(), this,
                initialTime.getHour(), initialTime.getMinute(), true
        );
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Bundle args;
        if((args = getArguments()) == null) {
            return;
        }

        TimeSetListener l = (TimeSetListener) args.getSerializable(Constants.TIMEPICK_LISTENER_KEY);

        if(l != null){
            l.onTimeSet(new Time24Hour(hourOfDay, minute));
        }
    }
}
