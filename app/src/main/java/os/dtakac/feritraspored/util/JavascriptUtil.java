package os.dtakac.feritraspored.util;

import android.content.res.AssetManager;

import java.io.IOException;

import os.dtakac.feritraspored.model.resources.ResourceManager;

public class JavascriptUtil {

    public static String FUNCTION_START = "(function(){";
    public static String FUNCTION_END = "}());\n";

    private static String SCROLL_INTO_VIEW_PATH = "scroll-into-view-script.txt";
    private static String P_CONTAINS_PATH = "p-contains-script.txt";
    private static String WEEKNUM_PATH = "week-num-script.txt";
    private static String HIDE_ALL_BUT_SCHEDULE_PATH = "hide-all-but-schedule-script.txt";
    private static String DARK_THEME_PATH = "dark-theme-script.txt";
    private static String TIME_BLOCKS_PATH = "time-on-blocks-script.txt";

    private AssetManager am;
    private ResourceManager rm;

    public JavascriptUtil(AssetManager assetManager, ResourceManager resManager){
        am = assetManager;
        rm = resManager;
    }

    private String buildScript(String[] args, String scriptPath){
        String script = "";
        try {
            script = FileUtil.readFile(am.open(scriptPath));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder b = new StringBuilder();
        b.append(FUNCTION_START).append("\n");
        for(String arg: args){
            b.append(String.format(script, arg));
        }
        b.append(FUNCTION_END);
        return b.toString();
    }

    public String highlightElementsScript(String[] filters){
        String pContains = "";
        String highlightScript = "";
        try {
            pContains = FileUtil.readFile(am.open(P_CONTAINS_PATH));
            highlightScript = FileUtil.readFile(am.open(rm.getHighlightScriptPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder b = new StringBuilder();
        for(String filter: filters){
            b.append(String.format(pContains, filter)).append(",");
        }
        b.deleteCharAt(b.length() - 1);

        return String.format(highlightScript, b.toString());
    }

    public String scrollIntoViewScript(String elementName){
        return buildScript(new String[]{elementName}, SCROLL_INTO_VIEW_PATH);
    }

    public String weekNumberScript(){
        try {
            return FUNCTION_START + FileUtil.readFile(am.open(WEEKNUM_PATH)) + FUNCTION_END;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String hideAllButScheduleScript(){
        try{
            return FUNCTION_START + FileUtil.readFile(am.open(HIDE_ALL_BUT_SCHEDULE_PATH)) + FUNCTION_END;
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public String darkThemeScript(){
        try{
            return FUNCTION_START + FileUtil.readFile(am.open(DARK_THEME_PATH)) + FUNCTION_END;
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public String timeOnBlocksScript(){
        try{
            return FUNCTION_START + FileUtil.readFile(am.open(TIME_BLOCKS_PATH)) + FUNCTION_END;
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }
}
