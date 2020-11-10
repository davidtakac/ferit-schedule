package os.dtakac.feritraspored.views.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AlertDialogFragment extends DialogFragment {

    private static String TITLE_KEY = "title_key";
    private static String CONTENT_KEY = "content_key";
    private static String POSITIVE_KEY = "dismiss_key";

    private AlertDialogFragment(){}

    public static AlertDialogFragment newInstance(int titleResId, int contentResId, int positiveResId){
        AlertDialogFragment f = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TITLE_KEY, titleResId);
        args.putInt(CONTENT_KEY, contentResId);
        args.putInt(POSITIVE_KEY, positiveResId);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getStringFromArgs(POSITIVE_KEY), (dialogInterface, i) -> dismiss());
        builder.setTitle(getStringFromArgs(TITLE_KEY));
        builder.setMessage(getStringFromArgs(CONTENT_KEY));
        return builder.create();
    }

    private CharSequence getStringFromArgs(String key){
        return getResources().getText(getArguments().getInt(key));
    }
}
