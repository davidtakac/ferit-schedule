package os.dtakac.feritraspored.common.view.dialog_info

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class InfoDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
    companion object {
        private const val KEY_TITLE = "title_key"
        private const val KEY_CONTENT = "content_key"
        private const val KEY_DISMISS = "dismiss_key"

        fun newInstance(
                @StringRes titleResId: Int,
                @StringRes contentResId: Int,
                @StringRes dismissResId: Int
        ): InfoDialogFragment {
            return InfoDialogFragment().also {
                it.arguments = Bundle().apply {
                    putInt(KEY_TITLE, titleResId)
                    putInt(KEY_CONTENT, contentResId)
                    putInt(KEY_DISMISS, dismissResId)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setPositiveButton(getText(requireArguments().getInt(KEY_DISMISS)), this)
                .setTitle(getText(requireArguments().getInt(KEY_TITLE)))
                .setMessage(getText(requireArguments().getInt(KEY_CONTENT)))
                .create()
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        dismiss()
    }
}