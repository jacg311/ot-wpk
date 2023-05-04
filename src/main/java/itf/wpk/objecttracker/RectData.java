package itf.wpk.objecttracker;

import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public record RectData(Rect[] rects, Scalar color) {}