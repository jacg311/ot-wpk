package itf.wpk.objecttracker;

import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.nio.file.Paths;

public class DetectionCheckbox extends JCheckBox {
    CascadeClassifier classifier;
    Scalar color;
    int id;

    public DetectionCheckbox(int id, String name, String filePath, int r, int g, int b) {
        super(name);
        this.id = id;
        this.classifier = new CascadeClassifier(Paths.get(".", filePath).toString());
        this.color = new Scalar(b, g, r);
        this.setName(name);
    }

    public Scalar getColor() {
        return color;
    }

    public CascadeClassifier getClassifier() {
        return classifier;
    }
}
