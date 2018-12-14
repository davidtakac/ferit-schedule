package os.dtakac.feritraspored.util;

import java.io.InputStream;
import java.util.Scanner;

public class FileUtil {

    public static String readFile(InputStream inputStream){
        Scanner s = new Scanner(inputStream);
        StringBuilder b = new StringBuilder();

        while(s.hasNextLine()){
            b.append(s.nextLine());
        }
        return b.toString();
    }
}
