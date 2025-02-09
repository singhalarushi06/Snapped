package com.example.snapped;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity implements ObjectDetectionListener {
    private static final String TAG = "CameraX";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private PreviewView previewView;
    private CameraSelector cameraSelector;
    private TextView objectText;
    private Button addToCart;
    private Button viewCart;
    private String itemsInCart = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.preview_view);
        objectText = findViewById(R.id.object_text);
        cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        addToCart = findViewById(R.id.add_to_cart_button);
        viewCart = findViewById(R.id.button2);

        addToCart.setOnClickListener(v -> itemsInCart = itemsInCart + "\n" + objectText.getText().toString().substring(0, objectText.getText().toString().indexOf(":")));

        viewCart.setOnClickListener(v -> {
            Intent intent = new Intent(CameraActivity.this, Cart.class);
            intent.putExtra("ITEMS", itemsInCart);
            startActivity(intent);
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> processCameraProviderListenableFuture =
                ProcessCameraProvider.getInstance(this);

        processCameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = processCameraProviderListenableFuture.get();
                cameraProvider.unbindAll();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .build();

                ExecutorService analysisExecutor = Executors.newSingleThreadExecutor();
                imageAnalysis.setAnalyzer(analysisExecutor, new ObjectDetection(this));

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Log.e(TAG, "Failed to start camera", e);
                Toast.makeText(this, "Failed to start camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onObjectsDetected(List<DetectedObject> objects) {
        runOnUiThread(() -> {
            if (!objects.isEmpty()) {
                for (DetectedObject object : objects) {
                    List<DetectedObject.Label> labels = object.getLabels();

                    // Find the label with the highest confidence score
                    DetectedObject.Label bestLabel = null;
                    float highestConfidence = 0.0f;

                    for (DetectedObject.Label label : labels) {
                        if (label.getConfidence() > highestConfidence) {
                            bestLabel = label;
                            highestConfidence = label.getConfidence();
                        }
                    }

                    if (bestLabel != null) {
                        String labelName = bestLabel.getText();
                        float labelConfidence = bestLabel.getConfidence();
                        if (labelConfidence > 0.3) {
                            objectText.setText(labelName + " : " + labelConfidence);
                        }
                    }
                }
            }
        });
    }
}