package os.dtakac.feritraspored.util;

public class JsUtil {

    private static final String P_CONTAINS = "p:contains(%s)";
    private static final String HIDE_ELEMENT = "document.getElementById(\"%s\").style.display = \"none\"";
    private static final String REMOVE_ELEMENT = "document.getElementById(\"%s\").remove();";
    private static final String SCROLL_INTO_VIEW = "document.getElementsByName(\"%s\")[0].scrollIntoView()";

    private static final String SEPARATOR = ",";

    /**
     * Parses comma separated string that contains different group ID's to
     * a series of connected JS p:contains([group]) queries. <br>
     *
     * e.g. for the input String of "PR-LV4,PR-2,4/16" the output will be: <br>
     * "p:contains("PR-LV4"),p:contains("PR-2"),p:contains("4/16")
     *
     * @param commaSeparatedFilters the input string to be parsed into series of p:contains queries.
     * @return String containing p:contains queries separated by comma(ready for JS injection).
     *
     */
    public static String toPContains(String commaSeparatedFilters){
        StringBuilder builder = new StringBuilder();
        String[] groups = commaSeparatedFilters.split(SEPARATOR);
        for(String group: groups){
            builder.append(String.format(P_CONTAINS,group)).append(SEPARATOR);
        }
        String result = builder.toString();
        //removes trailing separator
        return result.substring(0, result.length() - 1);
    }

    /**
     * Parses comma separated string that contains different element ID's to
     * a JS function which hides those elements.
     *
     * @param commaSeparatedIds the input comma separated string with ID's to be hidden.
     * @return JavaScript function.<br>
     *     e.g. for the input of "header,header-top" the return string will be:<br>
     *     function(){document.getElementById("header").style.display="none";document.getElementById("header-top").style.display="none";}
     */
    public static String toHideElementsWithIdFunction(String commaSeparatedIds){
        StringBuilder builder = new StringBuilder();
        builder.append("function(){");
        String[] ids = commaSeparatedIds.split(SEPARATOR);
        for(String id: ids){
            builder.append(String.format(HIDE_ELEMENT, id));
            builder.append(";");
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     * Parses comma separated string that contains IDs of elements to remove into a
     * JS function that removes those elements from the webpage.
     *
     * @param commaSeparatedIds comma separated IDs to be removed
     * @return JavaScript function that removes the elements. <br>
     *     e.g. for the input "header,footer" the following string is returned:<br>
     *     function(){document.getElementById("header").remove();document.getElementById("header-top").remove();}
     */
    public static String toRemoveElementsWithIdFunction(String commaSeparatedIds){
        StringBuilder builder = new StringBuilder();
        builder.append("function(){");
        String[] ids = commaSeparatedIds.split(SEPARATOR);
        for(String id: ids){
            builder.append(String.format(REMOVE_ELEMENT, id));
            builder.append(";");
        }
        builder.append("}");
        return builder.toString();
    }

    public static String toScrollIntoViewFunction(String viewId){
        return "function(){" + String.format(SCROLL_INTO_VIEW, viewId) + ";}";
    }
}
