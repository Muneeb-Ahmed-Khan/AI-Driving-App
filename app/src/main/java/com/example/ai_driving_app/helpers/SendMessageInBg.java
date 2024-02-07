package com.example.ai_driving_app.helpers;

import android.os.AsyncTask;
import android.util.Log;
import com.example.ai_driving_app.interfaces.BotReply;

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
      return "This is static response.";
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