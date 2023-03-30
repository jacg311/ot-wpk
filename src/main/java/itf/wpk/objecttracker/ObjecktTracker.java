package itf.wpk.objecttracker;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class ObjecktTracker {

    public static void init() {
        OpenCV.loadLocally();
        Mat loadedImage = loadImage(getPath("/TestImg.jpg"));
        MatOfRect facesDetected = new MatOfRect();

        //Cascade Classifier
        CascadeClassifier cascadeClassifier = new CascadeClassifier();
        int minFaceSize = Math.round(loadedImage.rows()* 0.1f);
            cascadeClassifier.load(getPath("/haarcascade_frontalface_default.xml"));
            cascadeClassifier.detectMultiScale(loadedImage,
        facesDetected,
                1.2,
                3,
        Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minFaceSize, minFaceSize),
                    new Size()
            );

        Rect[] facesArray = facesDetected.toArray();
            for(Rect face : facesArray) {
            Imgproc.rectangle(loadedImage, face.tl(), face.br(), new
                    Scalar(0,0,255), 3);
        }
    }


    public static Mat loadImage(String imagePath) {
        return Imgcodecs.imread(imagePath);
    }

    public static String getPath(String string) {
        try {
            return Paths.get(Main.class.getResource(string).toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
