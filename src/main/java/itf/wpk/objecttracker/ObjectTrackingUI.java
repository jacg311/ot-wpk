package itf.wpk.objecttracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.*;
import org.opencv.imgproc.*;

public class ObjectTrackingUI extends JFrame implements ActionListener {
    private JLabel videoOutput;
    private JComboBox<String> webcamSelector;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;
    private JPanel panelCheckbox;
    private VideoCapture videoCapture;
    private Mat frame = new Mat();
    private Timer timer;
    private CascadeClassifier faceCascade;
    private MatOfRect faces;
    private int cam_width = 640;
    private int cam_heigh = 480;
    private int new_width = cam_width;
    private int new_heigh = cam_heigh;

    public ObjectTrackingUI() {
        super("Object Tracking Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 480);
        setMinimumSize(new Dimension(640, 480));
        setLayout(new BorderLayout());

        // Create the video output label
        videoOutput = new JLabel();
        add(videoOutput, BorderLayout.CENTER);
        videoOutput.setHorizontalAlignment(0);
        videoOutput.setHorizontalTextPosition(0);

        // Create checkbox on Button
        panelCheckbox = new JPanel();
        add(panelCheckbox, BorderLayout.SOUTH);

        checkBox1 = new JCheckBox();
        checkBox1.setText("CheckBox");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        panelCheckbox.add(checkBox1, gbc);

        checkBox2 = new JCheckBox();
        checkBox2.setText("CheckBox");
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        panelCheckbox.add(checkBox2, gbc);

        checkBox3 = new JCheckBox();
        checkBox3.setText("CheckBox");
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        panelCheckbox.add(checkBox3, gbc);

        // Create the webcam selector combo box
        webcamSelector = new JComboBox<>();
        webcamSelector.addActionListener(this);
        add(webcamSelector, BorderLayout.NORTH);

        // Load the face cascade classifier
        faceCascade = new CascadeClassifier();

        faceCascade.load(Util.getPath("/haarcascade_frontalface_default.xml"));

        // Initialize the video capture
        videoCapture = new VideoCapture(0);

        webcamSelector.addItem("Webcam " + 0);

        if (webcamSelector.getItemCount() > 0) {
            videoCapture.read(frame);
            videoOutput.setIcon(new ImageIcon(mat2BufferedImage(frame).getScaledInstance(getWidth(),getHeight(),Image.SCALE_SMOOTH)));
        } else {
            JOptionPane.showMessageDialog(this, "No webcams detected.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Create the timer to update the video output
        timer = new Timer(10, this);
        timer.start();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == webcamSelector) {
            // Change the webcam
            videoCapture.release();
            videoCapture.open(webcamSelector.getSelectedIndex());
            videoCapture.read(frame);
            videoOutput.setIcon(new ImageIcon(mat2BufferedImage(frame)));
        } else {
            // Update the video output
            videoCapture.read(frame);
            Mat grayFrame = new Mat();
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
            faces = new MatOfRect();
            faceCascade.detectMultiScale(grayFrame, faces);
            for (Rect rect : faces.toArray()) {
                Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);
            }

            double scaleX = (double)getSize().width/cam_width;
            double scaleY = (double)getSize().height/cam_heigh;
            if (scaleX < scaleY) {  // heigh
                new_width = (int)(scaleX*cam_width);
                new_heigh = (int)(scaleX*cam_heigh);
            }
            else if (scaleY < scaleX) {  // width
                new_width = (int)(scaleY*cam_width);
                new_heigh = (int)(scaleY*cam_heigh);
            }
            else {
                new_width = getWidth();
                new_heigh = getHeight();
            }
            videoOutput.setIcon(new ImageIcon(mat2BufferedImage(frame).getScaledInstance(new_width,new_heigh,Image.SCALE_SMOOTH)));
        }
    }
    private BufferedImage mat2BufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b);
        BufferedImage img = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return img;
    }
}


