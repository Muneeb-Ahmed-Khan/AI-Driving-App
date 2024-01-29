package com.example.ai_driving_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class roadGuard extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "roadGuard";

    private CameraManager cameraManager;
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private SurfaceView surfaceView;

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    private Button captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_guard);

        surfaceView = findViewById(R.id.previewView);
        surfaceView.getHolder().addCallback(surfaceCallback);

        initViews();
        setupCamera();

        // Check and request camera permission
        if (checkCameraPermission()) {
            // Permission already granted
            setupCamera();
        } else {
            // Request camera permission
            requestCameraPermission();
        }
    }

    private void initViews() {
        surfaceView = findViewById(R.id.previewView);
        captureButton = findViewById(R.id.captureButton);

        // Set up capture button click listener
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
    }

    private void captureImage() {
        // Implement logic to capture image and save to file
        File imageFile = createImageFile();
        // Pass the image file path to a new activity
        displayCapturedImage(imageFile.getAbsolutePath());
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.e(TAG, "Error creating image file: " + e.getMessage());
            return null;
        }
    }

    private void displayCapturedImage(String imagePath) {
        Intent intent = new Intent(this, DisplayImageActivity.class);
        intent.putExtra("imagePath", imagePath);
        startActivity(intent);
    }
    private final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            // Surface is created or recreated
            if (checkCameraPermission()) {
                // Permission already granted
                setupCamera();
            }
            else {
                // Request camera permission
                requestCameraPermission();
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            // Surface properties changed (e.g., size)
            // Recreate camera session if needed
            recreateCameraSession();
        }

        private void recreateCameraSession() {
            if (cameraCaptureSession != null) {
                cameraCaptureSession.close();
                cameraCaptureSession = null;
                setupCamera();  // Recreate the camera session
            }
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            // Surface is destroyed
            // Release camera resources if needed
        }
    };


    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE
        );
    }

    @SuppressLint("WrongViewCast")
    private void setupCamera() {
        surfaceView = findViewById(R.id.previewView);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            // Get the front camera ID. You can modify this if you want the rear camera.
            cameraId = cameraManager.getCameraIdList()[0];

            // Close the camera if it's already open
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }

            // Open the camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(cameraId, cameraStateCallback, backgroundHandler);
                }
            }

        }

        catch (CameraAccessException e) {
            Log.e(TAG, "Error accessing camera: " + e.getMessage());
        }
    }

    private final CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            // Camera opened successfully
            cameraDevice = camera;

            // Create a preview session
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            // Handle camera disconnection
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            // Handle camera errors
            camera.close();
            cameraDevice = null;
        }
    };

    private void createCameraPreviewSession() {
        try {
            Surface surface = surfaceView.getHolder().getSurface();

            // Check if the surface is still valid
            if (surface.isValid()) {
                // Create a CaptureRequest for preview
                final CaptureRequest.Builder previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(surface);

                // Create a CameraCaptureSession for preview
                cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        if (cameraDevice == null) {
                            return;
                        }

                        // The camera is already closed
                        cameraCaptureSession = session;

                        // Start displaying the preview
                        try {
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                            session.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "Error starting camera preview: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Log.e(TAG, "Failed to configure camera session");
                    }
                }, backgroundHandler);
            } else {
                Log.e(TAG, "Surface is not valid. Unable to create camera preview session.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating camera preview session: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                Log.e(TAG, "Error stopping background thread: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private void releaseCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }

        if (surfaceView != null) {
            surfaceView.getHolder().getSurface().release();
        }

        startBackgroundThread();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted
                setupCamera();
            } else {
                // Camera permission denied
                Toast.makeText(this, "Camera permission is required for this app", Toast.LENGTH_SHORT).show();
                // Handle permission denial gracefully (e.g., show a message or close the app)
            }
        }
    }
}
