package os.dtakac.feritraspored.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import os.dtakac.feritraspored.model.programmes.Programme;

public class FileUtil {

    public static List<Programme> parseListOfProgrammes(InputStream inputStream){
        Scanner s = new Scanner(inputStream);
        List<Programme> result = new ArrayList<>();

        while(s.hasNextLine()){
            String[] values = s.nextLine().split(",");
            result.add(new Programme(values[0],values[1]));
        }

        return result;
    }

}
