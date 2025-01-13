package com.haswanth.aiassistant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText inputMessage;
    private ImageButton sendButton;

    private static final String API_URL = "https://androidchatbot.haswanthraj777.workers.dev/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);

        // Set up the RecyclerView and adapter
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Load initial messages
        loadInitialMessages();

        // Handle send button click
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = inputMessage.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    // Add user's message to the chat
                    messageList.add(new Message(userMessage, true)); // true for user
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);

                    // Clear the input field
                    inputMessage.setText("");

                    // Fetch AI response from API
                    fetchAIResponse(userMessage);
                }
            }
        });
    }

    private void loadInitialMessages() {
        messageList.add(new Message("Hi there! How can I assist you today?", false)); // AI message
        chatAdapter.notifyDataSetChanged();
    }

    private void fetchAIResponse(String userMessage) {
        OkHttpClient client = new OkHttpClient();


        // Prepare the JSON body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("prompt", userMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        RequestBody requestBody = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        // Build the request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "API call failed: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to get AI response", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        // Parse the AI response (assuming the response is plain text or JSON with a "response" field)
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String aiResponse = jsonResponse.optString("response", "Oops! I couldn't understand that.");

                        // Add the AI response to the chat
                        runOnUiThread(() -> {
                            messageList.add(new Message(aiResponse, false)); // false for AI
                            chatAdapter.notifyItemInserted(messageList.size() - 1);
                            chatRecyclerView.scrollToPosition(messageList.size() - 1);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error parsing AI response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e("MainActivity", "API call unsuccessful: " + response.code());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to get AI response", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
