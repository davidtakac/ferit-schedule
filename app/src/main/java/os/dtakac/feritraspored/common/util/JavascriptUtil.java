package os.dtakac.feritraspored.common.util;

import android.content.res.AssetManager;

import java.io.IOException;

import os.dtakac.feritraspored.R;
import os.dtakac.feritraspored.common.ResourceManager;

public class JavascriptUtil {

    private static String FUNCTION_START = "(function(){";
    private static String FUNCTION_END = "}());\n";

    private AssetManager ass; //heh
    private ResourceManager res;

    public JavascriptUtil(AssetManager assetManager, ResourceManager resManager){
        ass = assetManager;
        res = resManager;
    }

    private String buildScript(String[] args, String scriptPath){
        String script = "";
        try {
            script = FileUtil.readFile(ass.open(scriptPath));
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
            pContains = FileUtil.readFile(ass.open("p-contains-script.txt"));
            highlightScript = FileUtil.readFile(ass.open(res.get(R.string.highlight_script_path)));
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
        return buildScript(new String[]{elementName}, "scroll-into-view-script.txt");
    }

    public String weekNumberScript(){
        try {
            return FUNCTION_START + FileUtil.readFile(ass.open("week-num-script.txt")) + FUNCTION_END;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String hideAllButScheduleScript(){
        try{
            return FUNCTION_START + FileUtil.readFile(ass.open("hide-all-but-schedule-script.txt")) + FUNCTION_END;
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public String darkThemeScript(){
        try{
            return FUNCTION_START + FileUtil.readFile(ass.open("dark-theme-script.txt")) + FUNCTION_END;
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public String timeOnBlocksScript(){
        try{
            return FUNCTION_START + FileUtil.readFile(ass.open("time-on-blocks-script.txt")) + FUNCTION_END;
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }
}
