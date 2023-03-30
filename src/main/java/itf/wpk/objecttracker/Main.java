package itf.wpk.objecttracker;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

public class Main {
    public static void main(String[] args) {
        OpenCV.loadShared();
        Mat loadedImage = loadImage(Main.class.getResource("/TestImg.jpg").toString());
        MatOfRect facesDetected = new MatOfRect();

        //Cascade Classifier
        CascadeClassifier cascadeClassifier = new CascadeClassifier();
        int minFaceSize = Math.round(loadedImage.rows() * 0.1f);
        cascadeClassifier.load(Main.class.getResource("haarcascade_frontalface_default.xml").toString());
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
        saveImage(loadedImage, "C:\\Users");
    }

    public static Mat loadImage(String imagePath) {
        return Imgcodecs.imread(imagePath);
    }

    public static void saveImage(Mat imageMatrix, String targetPath) {
        Imgcodecs.imwrite(targetPath, imageMatrix);
    }
}