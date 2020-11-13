package os.dtakac.feritraspored.common.utils

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsIntent
import os.dtakac.feritraspored.R

fun bugReportIntent(resources: Resources): Intent {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, resources.getStringArray(R.array.email_addresses))
    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.subject_bug_report))
    return Intent.createChooser(intent, resources.getString(R.string.label_email_via))
}

fun openInExternalBrowserIntent(url: String): Intent {
    return Intent(Intent.ACTION_VIEW, Uri.parse(url))
}