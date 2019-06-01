package os.dtakac.feritraspored.ui.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import os.dtakac.feritraspored.R;

public class GroupsHelpDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getString(R.string.settings_groupshelp_dialogbutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        builder.setTitle(getString(R.string.settings_groupshelp_title));
        builder.setMessage(getString(R.string.settings_groupshelp_dialogtext));
        return builder.create();
    }
}
