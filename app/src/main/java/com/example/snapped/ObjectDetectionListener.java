package com.example.snapped;

import com.google.mlkit.vision.objects.DetectedObject;

import java.util.List;

public interface ObjectDetectionListener {
    void onObjectsDetected(List<DetectedObject> objects);
}
