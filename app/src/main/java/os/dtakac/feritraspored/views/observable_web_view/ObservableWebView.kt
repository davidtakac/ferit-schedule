package os.dtakac.feritraspored.views.observable_web_view

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class ObservableWebView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {
    var scrollListener: ScrollListener? = null

    override fun onScrollChanged(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY)
        when {
            scrollY > oldScrollY -> scrollListener?.onScrollDown()
            scrollY < oldScrollY -> scrollListener?.onScrollUp()
        }
        scrollListener?.onScroll(scrollX, scrollY, oldScrollX, oldScrollY)
    }

    interface ScrollListener{
        fun onScroll(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)
        fun onScrollDown()
        fun onScrollUp()
    }
}
