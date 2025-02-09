package com.example.snapped;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

public class ObjectDetection implements ImageAnalysis.Analyzer {

    private final ObjectDetectionListener listener;
    private static final LocalModel localModel = new LocalModel.Builder().setAssetFilePath("object_labeler.tflite").build();

    public ObjectDetection(ObjectDetectionListener listener) {
        this.listener = listener;
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        @OptIn(markerClass = ExperimentalGetImage.class)
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            detectObject(image, imageProxy);
        }
    }

    public void detectObject(InputImage inputImage, ImageProxy imageProxy) {
        CustomObjectDetectorOptions options = new CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                .enableClassification()
                .build();
        ObjectDetector objectDetector = com.google.mlkit.vision.objects.ObjectDetection.getClient(options);

        objectDetector.process(inputImage)
                .addOnSuccessListener(detectedObjects -> {
                    listener.onObjectsDetected(detectedObjects);
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    Log.d("HELPPPP", e.getMessage());
                    e.printStackTrace();
                    imageProxy.close();
                });
    }
}
