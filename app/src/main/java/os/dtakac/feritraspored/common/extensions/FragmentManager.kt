package os.dtakac.feritraspored.common.extensions

import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.constants.DIALOG_WHATS_NEW
import os.dtakac.feritraspored.views.dialog_info.InfoDialogFragment

fun FragmentManager.showInfoDialog(
        @StringRes titleResId: Int,
        @StringRes contentResId: Int,
        @StringRes dismissResId: Int = R.string.okay,
        key: String
) {
    val infoDialog = InfoDialogFragment.newInstance(titleResId, contentResId, dismissResId)
    infoDialog.show(this, key)
}

fun FragmentManager.showChangelog() {
    this.showInfoDialog(
            titleResId = R.string.title_whats_new,
            contentResId = R.string.content_whats_new,
            dismissResId = R.string.dismiss_whats_new,
            key = DIALOG_WHATS_NEW
    )
}