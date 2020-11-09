package os.dtakac.feritraspored.common;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import os.dtakac.feritraspored.R;

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
            script = readFile(ass.open(scriptPath));
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
        String highlightScript = "";
        try {
            highlightScript = readFile(ass.open(res.getString(R.string.highlight_script_path)));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        StringBuilder b = new StringBuilder();
        for(String filter: filters){
            b.append(String.format(highlightScript, filter));
        }
        return b.toString();
    }

    public String scrollIntoViewScript(String elementName){
        return buildScript(new String[]{elementName}, "scroll-into-view.js");
    }

    public String weekNumberScript(){
        try {
            return FUNCTION_START + readFile(ass.open("get-week-number.js")) + FUNCTION_END;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String hideAllButScheduleScript(){
        try{
            return FUNCTION_START + readFile(ass.open("hide-junk.js")) + FUNCTION_END;
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public String darkThemeScript(){
        try{
            return FUNCTION_START + readFile(ass.open("dark-theme.js")) + FUNCTION_END;
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public String timeOnBlocksScript(){
        try{
            return FUNCTION_START + readFile(ass.open("time-on-blocks.js")) + FUNCTION_END;
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    private String readFile(InputStream inputStream){
        Scanner s = new Scanner(inputStream);
        StringBuilder b = new StringBuilder();

        while(s.hasNextLine()){
            b.append(s.nextLine());
        }
        return b.toString();
    }
}
