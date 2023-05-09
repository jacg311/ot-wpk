package itf.wpk.objecttracker;

import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.JMarsDarkTheme;
import nu.pattern.OpenCV;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        Util.copyHaarCascadesFromJar();
        UIManager.setLookAndFeel(new MaterialLookAndFeel(new JMarsDarkTheme()));
        OpenCV.loadLocally();
        ObjectTrackingUI ui = new ObjectTrackingUI();
        ui.setVisible(true);
    }
}