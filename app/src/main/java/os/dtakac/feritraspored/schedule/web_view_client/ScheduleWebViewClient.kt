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
        fun onOverrideUrlLoading(request: WebResourceRequest?)
        fun onPageStarted()
        fun onErrorReceived(
                code: Int,
                description: String?,
                url: String?
        )
        fun onPageFinished(isError: Boolean)
    }

    private var isError: Boolean = false

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        listener.onOverrideUrlLoading(request)
        return true
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        listener.onPageStarted()
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        listener.onErrorReceived(errorCode, description, failingUrl)
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        listener.onPageFinished(isError)
        isError = false
        super.onPageFinished(view, url)
    }
}