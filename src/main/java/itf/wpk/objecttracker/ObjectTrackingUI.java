package itf.wpk.objecttracker;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ObjectTrackingUI extends JFrame implements ActionListener {
    private JLabel videoOutput;
    private JComboBox<String> webcamSelector;
    private JPanel panelCheckbox;
    private VideoCapture videoCapture;
    private Mat frame = new Mat();
    private Timer timer;
    private List<DetectionCheckbox> checkBoxes = new ArrayList<>();
    private int cam_width = 640;
    private int cam_heigh = 480;
    private int new_width = cam_width;
    private int new_heigh = cam_heigh;


    private final Executor executor = Executors.newFixedThreadPool(3);

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
        videoOutput.setText("fps: ??");
        videoOutput.setVerticalAlignment(1);
        videoOutput.setVerticalTextPosition(1);

        // Create checkbox on Button
        panelCheckbox = new JPanel();
        add(panelCheckbox, BorderLayout.SOUTH);

        // Create the webcam selector combo box
        webcamSelector = new JComboBox<>();
        webcamSelector.addActionListener(this);
        add(webcamSelector, BorderLayout.NORTH);

        checkBoxes.add(new DetectionCheckbox("Frontal Face Default", "/haarcascade_frontalface_default.xml", 255, 0, 0));
        checkBoxes.add(new DetectionCheckbox("Frontal Face Alt", "/haarcascade_frontalface_alt.xml", 0, 255, 0));
        checkBoxes.add(new DetectionCheckbox("Eyes", "/haarcascade_eye.xml", 0, 0, 255));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;

        checkBoxes.forEach(checkBox -> {
            checkBox.setSelected(true);
            panelCheckbox.add(checkBox, constraints);
        });

        // Initialize the video capture
        videoCapture = new VideoCapture(0);

        webcamSelector.addItem("Webcam " + 0);

        if (webcamSelector.getItemCount() > 0) {
            videoCapture.read(frame);
            videoOutput.setIcon(new ImageIcon(mat2BufferedImage(frame).getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH)));
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
            long frameTimeStart = System.currentTimeMillis();

            videoCapture.read(frame);
            Mat grayFrame = new Mat();
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

            CyclicBarrier barrier = new CyclicBarrier(4);
            List<RectData> rectDataList = Collections.synchronizedList(new ArrayList<>());

            for (var checkbox : checkBoxes) {
                executor.execute(() -> {
                    if (checkbox.isSelected()) {
                        MatOfRect faces = new MatOfRect();
                        checkbox.getClassifier().detectMultiScale(grayFrame, faces);

                        rectDataList.add(new RectData(faces.toArray(), checkbox.getColor()));
                    }

                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                throw new RuntimeException(ex);
            }

            for (RectData rectData : rectDataList) {
                for (Rect rect : rectData.rects()) {
                    Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), rectData.color(), 2);
                }
            }

            long frameTimeNew = System.currentTimeMillis() - frameTimeStart;
            //System.out.println(frameTimeNew);
            videoOutput.setText("ms: " + String.valueOf(frameTimeNew));

            double scaleX = (double) getSize().width / cam_width;
            double scaleY = (double) getSize().height / cam_heigh;
            if (scaleX < scaleY) {  // heigh
                new_width = (int) (scaleX * cam_width);
                new_heigh = (int) (scaleX * cam_heigh);
            } else if (scaleY < scaleX) {  // width
                new_width = (int) (scaleY * cam_width);
                new_heigh = (int) (scaleY * cam_heigh);
            } else {
                new_width = getWidth();
                new_heigh = getHeight();
            }

            videoOutput.setIcon(new ImageIcon(mat2BufferedImage(frame).getScaledInstance(new_width, new_heigh, Image.SCALE_SMOOTH)));
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


