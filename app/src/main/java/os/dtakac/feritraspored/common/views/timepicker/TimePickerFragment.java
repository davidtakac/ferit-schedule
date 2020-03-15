package os.dtakac.feritraspored.common.views.timepicker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.common.util.Constants;

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

        TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, initialTime.getHour(), initialTime.getMinute(), true);
        tpd.setButton(TimePickerDialog.BUTTON_NEGATIVE, getString(R.string.label_back), tpd);
        tpd.setButton(TimePickerDialog.BUTTON_POSITIVE, getString(R.string.label_save), tpd);

        return tpd;
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
