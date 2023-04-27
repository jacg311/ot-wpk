package itf.wpk.objecttracker;

import nu.pattern.OpenCV;
public class Main {

    public static void main(String[] args) {
        OpenCV.loadLocally();
        ObjectTrackingUI ui = new ObjectTrackingUI();
        ui.setVisible(true);
    }
}