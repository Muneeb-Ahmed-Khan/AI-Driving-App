package com.example.ai_driving_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000; // 3 seconds
    private static String OPENCV_LOG = "OPENCV_LOG";

    // Dynamically load the C++ library into application.
    static {
       System.loadLibrary("ai_driving_app");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        String hello = stringFromJNI();
        Log.d(OPENCV_LOG, hello);

        SaveDNNModel("best.onnx");

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

    /**
     * A native method that is implemented by the 'ai_driving_app' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
