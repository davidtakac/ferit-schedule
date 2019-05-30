package os.dtakac.feritraspored.util;

import android.content.res.AssetManager;

import java.io.IOException;

public class JavascriptUtil {

    private static String FUNCTION_START = "(function(){";
    private static String FUNCTION_END = "}());\n";

    private static String HIDE_CLASS_PATH = "hide-class-script.txt";
    private static String HIDE_ID_PATH = "hide-id-script.txt";
    private static String REMOVE_ID_PATH = "remove-id-script.txt";
    private static String SCROLL_INTO_VIEW_PATH = "scroll-into-view-script.txt";
    private static String P_CONTAINS_PATH = "p-contains-script.txt";
    private static String HIGHLIGHT_PATH = "highlight-paragraphs-script.txt";
    private static String ID_INVERT_PATH = "filter-invert-id-script.txt";
    private static String CLASS_INVERT_PATH = "filter-invert-class-script.txt";
    private static String CLASS_BACKGROUND_PATH = "class-background-script.txt";

    private AssetManager am;

    public JavascriptUtil(AssetManager assetManager){
        am = assetManager;
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

    public String changeClassesBackground(String[] classNames, String[] colors){
        if(classNames.length != colors.length){
            throw new IllegalStateException("There must be as many colors as there are class names and vice-versa.");
        }

        String script = "";
        try {
            script = FileUtil.readFile(am.open(CLASS_BACKGROUND_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder b = new StringBuilder();
        b.append(FUNCTION_START).append("\n");
        for(int i = 0; i < classNames.length; i++){
            b.append(String.format(script, classNames[i], colors[i]));
        }
        b.append(FUNCTION_END);
        return b.toString();
    }

    public String invertElementsColor(String[] elementIds, String invertValue){
        String script = "";
        try {
            script = FileUtil.readFile(am.open(ID_INVERT_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder b = new StringBuilder();
        b.append(FUNCTION_START).append("\n");
        for(String id: elementIds){
            b.append(String.format(script, id, invertValue));
        }
        b.append(FUNCTION_END);
        return b.toString();
    }

    public String invertClassesColor(String[] classNames, String invertValue){
        String script = "";
        try {
            script = FileUtil.readFile(am.open(CLASS_INVERT_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder b = new StringBuilder();
        b.append(FUNCTION_START).append("\n");
        for(String c: classNames){
            b.append(String.format(script, c, invertValue));
        }
        b.append(FUNCTION_END);
        return b.toString();
    }

    public String highlightElementsScript(String[] filters){
        String pContains = "";
        String highlightScript = "";
        try {
            pContains = FileUtil.readFile(am.open(P_CONTAINS_PATH));
            highlightScript = FileUtil.readFile(am.open(HIGHLIGHT_PATH));
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

    public String hideClassesScript(String[] classNames){
        return buildScript(classNames, HIDE_CLASS_PATH);
    }

    public String hideElementsScript(String[] elementIds){
        return buildScript(elementIds, HIDE_ID_PATH);
    }

    public String removeElementsScript(String[] elementIds){
        return buildScript(elementIds, REMOVE_ID_PATH);
    }
}
