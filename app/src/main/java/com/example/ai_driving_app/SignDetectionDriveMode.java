package com.example.ai_driving_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignDetectionDriveMode extends AppCompatActivity {

    private static final String TAG = "CameraXGFG";
    private static final int REQUEST_CODE_PERMISSIONS = 20;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private ImageView imageView;
    private ExecutorService cameraExecutor;
    private ProcessCameraProvider cameraProvider;
    private ImageAnalysis imageAnalysis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_detection_drive_mode);

        // hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Check camera permissions, if all permissions granted
        // start the camera, else ask for the permission
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        imageView = findViewById(R.id.imageView);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }
    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                cameraProvider = cameraProviderFuture.get();

                // Choose the camera by requiring a lens facing
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                imageAnalysis = new ImageAnalysis.Builder()
                                // enable the following line if RGBA output is needed.
                                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {

                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {

                        // Convert Android Image to OpenCV Mat
                        Mat mat = new Mat(imageProxy.getHeight(), imageProxy.getWidth(), CvType.CV_8UC4);
                        Utils.bitmapToMat(imageProxy.toBitmap(), mat);

                        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2BGR);
                        // Rotate 90Deg and flip the image.
                        Core.transpose(mat, mat);
                        Core.flip(mat, mat, 1);

                        //Procesing
                        String[] labels = processFrameFromJNI(mat.getNativeObjAddr());
                        for(String label: labels){
                            // Log.d(TAG, "Label Found: "  + label);
                        }

                        // Convert from BGR to RGB using Imgproc.COLOR_RGB2BGR, Android works on RGB
                        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
                        // Assuming you have a Bitmap object
                        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(mat,bitmap);
                        // Display the processed image
                        runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                        mat.release();

                        // after done, release the ImageProxy object
                        imageProxy.close();
                    }
                });

                // Attach use cases to the camera with the same lifecycle owner
                Camera camera = cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) this),
                        cameraSelector,
                        imageAnalysis);


            } catch (InterruptedException | ExecutionException e) {
                // Currently no exceptions thrown. cameraProviderFuture.get()
                // shouldn't block since the listener is being called, so no need to
                // handle InterruptedException.
            }
        }, ContextCompat.getMainExecutor(this));

    }

    private boolean allPermissionsGranted() {
        return Arrays.stream(REQUIRED_PERMISSIONS)
                .allMatch(permission -> ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED);
    }

    // checks the camera permission
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // If all permissions granted, then start the camera
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                // If permissions are not granted,
                // present a toast to notify the user that
                // the permissions were not granted.
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void releaseCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }

        // Close the camera executor
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private native String[] processFrameFromJNI(long mat);

}
