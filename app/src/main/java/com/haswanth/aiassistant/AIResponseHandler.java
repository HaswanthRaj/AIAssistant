package com.haswanth.aiassistant;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class AIResponseHandler {
    private static final String API_URL = "https://androidchatbot.haswanthraj777.workers.dev/";
    private static final OkHttpClient client = new OkHttpClient();

    public interface AIResponseCallback {
        void onSuccess(String response);
        void onFailure(String errorMessage);
    }

    public static void fetchAIResponse(String userMessage, AIResponseCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("prompt", userMessage);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure("JSON Error");
            return;
        }

        RequestBody requestBody = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AIResponseHandler", "API call failed: " + e.getMessage());
                callback.onFailure("Failed to get AI response");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONObject responseObject = jsonResponse.optJSONObject("response");
                        String aiResponse = responseObject != null ?
                                responseObject.optString("response", "Oops! I couldn't understand that.") :
                                "Oops! I couldn't understand that.";

                        callback.onSuccess(aiResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("Error parsing AI response");
                    }
                } else {
                    callback.onFailure("API call unsuccessful: " + response.code());
                }
            }
        });
    }
}
