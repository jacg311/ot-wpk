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
    private List<DetectionCheckbox> checkBoxes = new ArrayList<>();
    private int camWidth = 640;
    private int camHeight = 480;
    private int newWidth = camWidth;
    private int newHeight = camHeight;


    private final Executor executor = Executors.newFixedThreadPool(3);
    private final List<RectData> rectDataList = Collections.synchronizedList(new ArrayList<>());
    CyclicBarrier barrier = new CyclicBarrier(4);

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
        webcamSelector = new JComboBox<>() {
            @Override
            protected void paintComponent(Graphics g) {
                // Draw text with antialiasing
                Graphics2D graphics2d = (Graphics2D) g;
                graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };

        webcamSelector.addActionListener(this);
        add(webcamSelector, BorderLayout.NORTH);

        checkBoxes.add(new DetectionCheckbox(0, "Frontal Face Default", "/haarcascade_frontalface_default.xml", 255, 0, 0));
        checkBoxes.add(new DetectionCheckbox(1, "Frontal Face Alt", "/haarcascade_frontalface_alt.xml", 0, 255, 0));
        checkBoxes.add(new DetectionCheckbox(2, "Eyes", "/haarcascade_eye.xml", 0, 0, 255));

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
        Timer timer = new Timer(10, this);
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

            for (var checkbox : checkBoxes) {
                executor.execute(() -> {
                    if (checkbox.isSelected()) {
                        MatOfRect faces = new MatOfRect();
                        checkbox.getClassifier().detectMultiScale(grayFrame, faces);
                        rectDataList.add(new RectData(faces.toArray(), checkbox.getColor()));
                    }

                    _barrier(barrier);
                });
            }

            _barrier(barrier);

            for (RectData rectData : rectDataList) {
                for (Rect rect : rectData.rects()) {
                    Imgproc.rectangle(frame,
                            new Point(rect.x, rect.y),
                            new Point(rect.x + rect.width, rect.y + rect.height),
                            rectData.color(),
                            2
                    );
                }
            }

            rectDataList.clear();

            long frameTimeNew = System.currentTimeMillis() - frameTimeStart;
            videoOutput.setText("ms: " + frameTimeNew);

            double scaleX = (double) getSize().width / camWidth;
            double scaleY = (double) getSize().height / camHeight;
            if (scaleX < scaleY) {  // heigh
                newWidth = (int) (scaleX * camWidth);
                newHeight = (int) (scaleX * camHeight);
            } else if (scaleY < scaleX) {  // width
                newWidth = (int) (scaleY * camWidth);
                newHeight = (int) (scaleY * camHeight);
            } else {
                newWidth = getWidth();
                newHeight = getHeight();
            }

            videoOutput.setIcon(new ImageIcon(mat2BufferedImage(frame).getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)));
        }

    }

    private void _barrier(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            throw new RuntimeException(ex);
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


