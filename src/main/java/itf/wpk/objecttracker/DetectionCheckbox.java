package itf.wpk.objecttracker;

import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;

public class DetectionCheckbox extends JCheckBox {
    CascadeClassifier classifier;
    Scalar color;

    public DetectionCheckbox(String name, String filePath, int r, int g, int b) {
        super(name);
        this.classifier = new CascadeClassifier(Util.getPath(filePath));
        this.color = new Scalar(b, g, r);
    }

    public Scalar getColor() {
        return color;
    }

    public CascadeClassifier getClassifier() {
        return classifier;
    }
}
