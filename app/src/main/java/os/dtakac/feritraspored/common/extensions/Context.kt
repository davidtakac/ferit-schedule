package os.dtakac.feritraspored.common.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import os.dtakac.feritraspored.R
import os.dtakac.feritraspored.common.constants.SUPPORT_EMAILS

fun Context.openEmailEditor(
        subject: String = "",
        content: String = ""
) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, SUPPORT_EMAILS)
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, content)
    startActivity(Intent.createChooser(intent, resources.getString(R.string.label_email_via)))
}

fun Context.getColorCompat(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}