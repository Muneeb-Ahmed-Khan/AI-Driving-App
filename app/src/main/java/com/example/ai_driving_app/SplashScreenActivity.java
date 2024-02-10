package com.example.ai_driving_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000; // 3 seconds
    private static String OPENCV_LOG = "OPENCV_LOG";

    // Dynamically load the C++ library into application.
    static {
       System.loadLibrary("ai_driving_app");
    }

    private static final int REQUEST_CODE_PERMISSIONS = 1;

    private static String[] storage_permissions = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        if (allPermissionsGranted()) {
            initializeOpenCV();
        } else {
            ActivityCompat.requestPermissions(this, permissions(), REQUEST_CODE_PERMISSIONS);
        }
    }

    void initializeOpenCV(){

        SaveDNNModel("best.onnx");

        if(OpenCVLoader.initLocal()){
            Log.d(OPENCV_LOG, "OPENCV JAVA Loaded Successfully.");
            loadOnnxModel(this.getFilesDir().getAbsolutePath() + "/best.onnx");
        }
        else{
            Log.d(OPENCV_LOG, "OPENCV JAVA ---- DİD NOT LOAD");
        }

        String hello = stringFromJNI();
        Log.d(OPENCV_LOG, hello);

        // Use a Handler to delay the opening of the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your main activity
                Intent mainIntent = new Intent(SplashScreenActivity.this, Login.class);
                startActivity(mainIntent);
                finish(); // Close the splash screen activity
            }
        }, SPLASH_TIMEOUT);
    }
    void SaveDNNModel(String filename){
        File modelfile = new File(this.getFilesDir(), filename);
        Log.d(OPENCV_LOG, "************* File Loading ************");

        try{
            if(!modelfile.exists()){
                Log.d(OPENCV_LOG, "Model does not exists, Loading Model.");
                InputStream inputStream = getResources().openRawResource(R.raw.best);
                FileOutputStream outputStream = new FileOutputStream(modelfile);
                byte[] buffer = new byte[2048];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
            }
            else {
                Log.d(OPENCV_LOG, "Model already exists.");
            }
            Log.d(OPENCV_LOG, "Model Path: " +  modelfile.getAbsolutePath());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    private boolean allPermissionsGranted() {
        return Arrays.stream(permissions())
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
                initializeOpenCV();
            } else {
                // If permissions are not granted,
                // present a toast to notify the user that
                // the permissions were not granted.
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * A native method that is implemented by the 'ai_driving_app' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native boolean loadOnnxModel(String modelPath);
}
