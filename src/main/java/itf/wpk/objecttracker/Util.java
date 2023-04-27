package itf.wpk.objecttracker;

import javax.swing.*;
import java.awt.*;
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

    public static ImageIcon scaleImage(ImageIcon icon, int maxWidth, int maxHeight) {
        int originalWidth = icon.getIconWidth();
        int originalHeigth = icon.getIconHeight();
        int newWidth = originalWidth;
        int newHeight = originalHeigth;

        if (originalWidth > maxWidth) {
            newWidth = maxWidth;
            newHeight = (newWidth * originalHeigth) / originalWidth;
        }

        if (newHeight > maxHeight) {
            //scale height to fit instead
            newHeight = maxHeight;
            //scale width to maintain aspect ratio
            newWidth = (newHeight * originalWidth) / originalHeigth;
        }

        return new ImageIcon(icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT));
    }
}
