package itf.wpk.objecttracker;
import itf.wpk.objecttracker.ObjectTrackingUI;
import nu.pattern.OpenCV;

import java.io.*;
import javax.swing.*;
public class Main {

    public static void main(String[] args) {
        OpenCV.loadLocally();
        ObjectTrackingUI ui = new ObjectTrackingUI();
        ui.setVisible(true);
    }
}