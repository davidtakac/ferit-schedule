package os.dtakac.feritraspored.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import os.dtakac.feritraspored.R

fun Context.openBugReport(content: String = "") {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, resources.getStringArray(R.array.email_addresses))
    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.subject_bug_report))
    intent.putExtra(Intent.EXTRA_TEXT, content)
    startActivity(Intent.createChooser(intent, resources.getString(R.string.label_email_via)))
}