package com.haswanth.aiassistant;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SpeechRecognitionActivity extends AppCompatActivity {
    private SpeechRecognizer speechRecognizer;
    private TextView recognizedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_recognition);

        recognizedTextView = findViewById(R.id.Text);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    recognizedTextView.setText("You: " + recognizedText);

                    // Get AI response and send to Eleven Labs
                    AIResponseHandler.fetchAIResponse(recognizedText, new AIResponseHandler.AIResponseCallback() {
                        @Override
                        public void onSuccess(String aiResponse) {
                            runOnUiThread(() -> recognizedTextView.append("\nAI: " + aiResponse));

                            // Send AI response to Eleven Labs for speech synthesis
                            ElevenLabsTextToSpeech.speak(aiResponse, SpeechRecognitionActivity.this);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            runOnUiThread(() -> Toast.makeText(SpeechRecognitionActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            }

            @Override public void onError(int error) {
                Toast.makeText(SpeechRecognitionActivity.this, "Speech recognition error", Toast.LENGTH_SHORT).show();
            }
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });

        findViewById(R.id.startListeningButton).setOnClickListener(v -> startListening());
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizer.startListening(intent);
    }
}
