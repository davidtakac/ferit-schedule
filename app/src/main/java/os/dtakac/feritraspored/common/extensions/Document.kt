package os.dtakac.feritraspored.common.extensions

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

fun Element.addToStyle(style: String) {
    attr("style", attr("style") + "; $style")
}

fun Elements.addToStyle(style: String) {
    attr("style", attr("style") + "; $style")
}

fun Element.setBackgroundColor(colorHex: String) {
    addToStyle("background-color: $colorHex;")
}

fun Elements.setBackgroundColor(colorHex: String) {
    addToStyle("background-color: $colorHex;")
}

fun Elements.setColor(colorHex: String) {
    addToStyle("color: $colorHex;")
}