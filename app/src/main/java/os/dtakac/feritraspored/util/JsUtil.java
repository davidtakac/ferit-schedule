package os.dtakac.feritraspored.util;

public class JsUtil {

    private static final String P_CONTAINS = "p:contains(%s)";
    private static final String SEPARATOR = ",";

    /**
     * Parses comma separated string that contains different group ID's to
     * a series of connected JS p:contains([group]) queries. <br>
     * e.g. for the input String of "PR-LV4,PR-2,4/16" the output will be: <br>
     * "p:contains("PR-LV4"),p:contains("PR-2"),p:contains("4/16")
     *
     */
    public static String parseToPContains(String commaSeparatedFilters){
        StringBuilder builder = new StringBuilder();
        String[] groups = commaSeparatedFilters.split(SEPARATOR);
        for(String group: groups){
            builder.append(String.format(P_CONTAINS,group)).append(SEPARATOR);
        }
        String result = builder.toString();
        //removes trailing separator
        return result.substring(0, result.length() - 1);
    }


}
