package os.dtakac.feritraspored.schedule.web_view_client

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class ScheduleWebViewClient(
        private val listener: Listener
): WebViewClient() {
    interface Listener {
        fun onOverrideUrlLoading(url: String)
        fun onPageStarted()
        fun onErrorReceived(
                code: Int,
                description: String?,
                url: String?
        )
        fun onPageFinished(isError: Boolean)
    }

    private var isError: Boolean = false
    /*Handles calling [listener#onPageFinished] only if page was actually started before.
    * This fixes the odd problem where upon configuration change onPageFinished gets called without
    * onPageStarted being called first.*/
    private var wasStarted: Boolean = false

    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
        listener.onOverrideUrlLoading(url)
        return true
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        wasStarted = true
        listener.onPageStarted()
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        listener.onErrorReceived(errorCode, description, failingUrl)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if(wasStarted) listener.onPageFinished(isError)
        isError = false
    }
}