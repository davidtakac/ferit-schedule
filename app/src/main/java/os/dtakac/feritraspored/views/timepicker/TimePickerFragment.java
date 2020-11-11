package os.dtakac.feritraspored.views.timepicker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.common.constants.ConstantsKt;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static TimePickerFragment newInstance(Time24Hour initialTime, TimeSetListener listener) {
        Bundle args = new Bundle();
        args.putSerializable(ConstantsKt.LISTENER_TIME_PICKER, listener);
        args.putSerializable(ConstantsKt.DATA_TIME_PICKER, initialTime);

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
            initialTime = (Time24Hour) args.getSerializable(ConstantsKt.DATA_TIME_PICKER);
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

        TimeSetListener l = (TimeSetListener) args.getSerializable(ConstantsKt.LISTENER_TIME_PICKER);

        if(l != null){
            l.onTimeSet(new Time24Hour(hourOfDay, minute));
        }
    }
}
