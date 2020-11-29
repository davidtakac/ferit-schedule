package os.dtakac.feritraspored.common.extensions

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

fun Element.setBackgroundColor(colorHex: String) {
    setStyle("background-color: $colorHex;")
}

fun Element.setStyle(style: String) {
    attr("style", style)
}

fun Elements.setBackgroundColor(colorHex: String) {
    setStyle("background-color: $colorHex;")
}

fun Elements.setColor(colorHex: String) {
    setStyle("color: $colorHex;")
}

fun Elements.setStyle(style: String) {
    attr("style", style)
}