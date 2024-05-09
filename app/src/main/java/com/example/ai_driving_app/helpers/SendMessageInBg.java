package com.example.ai_driving_app.helpers;

import android.os.AsyncTask;
import android.util.Log;
import com.example.ai_driving_app.interfaces.BotReply;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendMessageInBg extends AsyncTask<Void, Void, String> {
  private String queryInput;
  private String TAG = "CHATBOT";
  private BotReply botReply;

  public SendMessageInBg(BotReply botReply, String queryInput) {
    this.botReply = botReply;
    this.queryInput = queryInput;
    Log.d(TAG, "doInBackground SendMessageInBg: " + queryInput);
  }

  @Override
  protected String doInBackground(Void... voids) {
    try {
      Log.d(TAG, "doInBackground: " + queryInput);

      // Define the request URL
      URL url = new URL("http://109-74-198-226.ip.linodeusercontent.com:8080");

      // Open connection
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      // Set request method
      connection.setRequestMethod("POST");

      // Set request headers
      connection.setRequestProperty("Content-Type", "text/plain");

      // Enable output and disable caching
      connection.setDoOutput(true);
      connection.setUseCaches(false);

      // Create request body
      String requestBody = "{\r\n    \"query\": \"" + queryInput + "\"\r\n}\r\n";

      // Write request body
      try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
        wr.writeBytes(requestBody);
        wr.flush();
      }

      // Get response code
      int responseCode = connection.getResponseCode();

      // Check if response code is successful
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
          String line;
          while ((line = in.readLine()) != null) {
            response.append(line);
          }
        }
        return response.toString();
      } else {
        return "Error: " + responseCode + " " + connection.getResponseMessage();
      }

    } catch (Exception e) {
      Log.d(TAG, "doInBackground Exception: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(String response) {
    //handle return response here
    botReply.callback(response);
  }
}