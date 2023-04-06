package itf.wpk.objecttracker;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Util {
    public static String getPath(String string) {
        try {
            return Paths.get(Main.class.getResource(string).toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
