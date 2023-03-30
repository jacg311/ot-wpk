package itf.wpk.objecttracker;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import javax.swing.filechooser.FileSystemView;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        OpenCV.loadLocally();
        Mat loadedImage = loadImage("./src/main/resources/TestImg.jpg");
        MatOfRect facesDetected = new MatOfRect();

        //Cascade Classifier
        CascadeClassifier cascadeClassifier = new CascadeClassifier();
        int minFaceSize = Math.round(loadedImage.rows() * 0.1f);
        cascadeClassifier.load("./src/main/resources/haarcascade_frontalface_default.xml");
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
        String savePath = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "/result.jpg";
        saveImage(loadedImage, savePath);
        System.out.println(savePath+" wurde gespeichert!");
    }

    public static Mat loadImage(String imagePath) {
        return Imgcodecs.imread(imagePath);
    }

    public static void saveImage(Mat imageMatrix, String targetPath) {
        Imgcodecs.imwrite(targetPath, imageMatrix);
    }
}