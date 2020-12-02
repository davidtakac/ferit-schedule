package os.dtakac.feritraspored.common.extensions

import org.jsoup.nodes.Element

fun Element.addToStyle(style: String) {
    attr("style", attr("style") + "; $style")
}