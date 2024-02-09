package com.example.ai_driving_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignDetection extends AppCompatActivity {

    private static final String TAG = "CameraXGFG";
    private static final int REQUEST_CODE_PERMISSIONS = 20;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ImageView imageView;
    private Button cameraCaptureButton;
    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private ProcessCameraProvider cameraProvider;
    private File outputFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_detection_picture_mode);

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

        previewView = findViewById(R.id.previewView);
        imageView = findViewById(R.id.imageView);
        cameraCaptureButton = findViewById(R.id.camera_capture_button);


        // set on click listener for the button to capture a photo
        // it calls a method which is implemented below
        cameraCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle button click event here
                takePhoto(); // Replace with your desired functionality
            }
        });

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void takePhoto() {
        // Get a stable reference of the
        // modifiable image capture use case
        if (imageCapture == null) {
            return;
        }

        // Capture the image using ImageCapture
        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(createImageFile()).build();

        // Set up image capture listener,
        // which is triggered after the photo has been taken
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e(TAG, "Photo capture failed: " + exc.getMessage(), exc);
                    }

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        showCapturedImage(output.getSavedUri());
                    }
                });
    }

    private void showCapturedImage(Uri savedUri) {
        // process the image from savedUri in Opencv
        String hello = stringFromJNI();
        Log.d(TAG, hello);

        // Display pure image untill processing is done.
        imageView.setImageURI(savedUri);

        // It will read it in BGR Mode.
        Mat imageMat = Imgcodecs.imread(savedUri.getPath());

        //Procesing
        String[] labels = processFrameFromJNI(imageMat.getNativeObjAddr());
        for(String label: labels){
            Log.d(TAG, "Label Found: "  + label);
        }

        // Convert from BGR to RGB using Imgproc.COLOR_RGB2BGR, Android works on RGB
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2RGB);

        // Convert OpenCV Mat to Android Bitmap
        Bitmap bitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(imageMat, bitmap);

        // Display the Bitmap in the ImageView
        imageView.setImageBitmap(bitmap);

        // Release the Mat when done
        imageMat.release();

        // Display the processed image in the ImageView
        // imageView.setImageURI(savedUri);
        showImagePreview();
    }

    private File createImageFile() {
        // Create a unique file name using a timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Get the external storage directory
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            return null;
        }

        // Create the image file
        try {
            File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            // Save the file path for later use
//            currentImagePath = imageFile.getAbsolutePath();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                cameraProvider = cameraProviderFuture.get();

                // Set up the view finder use case to display camera preview
                Preview preview = new Preview.Builder().build();

                // Set up the capture use case to allow users to take photos
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                // Choose the camera by requiring a lens facing
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Attach use cases to the camera with the same lifecycle owner
                Camera camera = cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) this),
                        cameraSelector,
                        preview,
                        imageCapture);

                // Connect the preview use case to the previewView
                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider());
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


    @Override
    public void onBackPressed() {
        // Check if ImageView is visible
        if (imageView.getVisibility() == View.VISIBLE) {
            // If ImageView is visible, hide it and show camera preview
            showCameraPreview();
        } else {
            // If ImageView is not visible, perform the default back button behavior
            super.onBackPressed();
        }
    }

    private void showCameraPreview() {
        imageView.setVisibility(View.GONE);
        previewView.setVisibility(View.VISIBLE);
        cameraCaptureButton.setVisibility(View.VISIBLE);
    }

    private void showImagePreview() {
        // Show the ImageView and hide the PreviewView
        imageView.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.GONE);
        cameraCaptureButton.setVisibility(View.GONE);
    }

    public native String stringFromJNI();
    private native String[] processFrameFromJNI(long mat);
}
