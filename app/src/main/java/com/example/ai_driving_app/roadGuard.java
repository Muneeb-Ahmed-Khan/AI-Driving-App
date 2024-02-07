package com.example.ai_driving_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class roadGuard extends AppCompatActivity {

    private ImageCapture imageCapture;
    private File outputDirectory;
    private ExecutorService cameraExecutor;

    private static final String TAG = "CameraXGFG";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final int REQUEST_CODE_PERMISSIONS = 20;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    Button cameraCaptureButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_guard);

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

        outputDirectory = getOutputDirectory();
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void takePhoto() {
        // Get a stable reference of the
        // modifiable image capture use case
        if (imageCapture == null) {
            return;
        }

        // Create time-stamped output file to hold the image
        File photoFile = new File(
                outputDirectory,
                new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        );

        // Create output options object which contains file + metadata
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

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
                        Uri savedUri = Uri.fromFile(photoFile);

                        // set the saved URI to the image view
                        ImageView imageView = findViewById(R.id.iv_capture);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageURI(savedUri);

                        String msg = "Photo capture succeeded: " + savedUri;
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        Log.d(TAG, msg);
                    }
                });
    }

    private void startCamera() {

        PreviewView previewView = findViewById(R.id.viewFinder);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

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

    // creates a folder inside internal storage
    private File getOutputDirectory() {
        File mediaDir = Arrays.stream(getExternalMediaDirs())
                .filter(it -> it != null)
                .findFirst()
                .map(it -> new File(it, getResources().getString(R.string.app_name)))
                .orElse(getFilesDir());

        if (mediaDir != null && !mediaDir.exists()) {
            mediaDir.mkdirs();
        }
        return mediaDir != null ? mediaDir : getFilesDir();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
