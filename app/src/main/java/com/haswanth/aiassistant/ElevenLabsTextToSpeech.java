package com.haswanth.aiassistant;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ElevenLabsTextToSpeech {
    private static final String API_KEY = "sk_ea2a9bb1cbbeb6e6796029b02a3571bae61154343c174154";
    private static final String VOICE_ID = "21m00Tcm4TlvDq8ikWAM";
    private static final String API_URL = "https://api.elevenlabs.io/v1/text-to-speech/" + VOICE_ID;

    private static final OkHttpClient client = new OkHttpClient();
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void speak(String text, Context context) {
        executorService.execute(() -> {
            try {
                File audioFile = fetchAudioFile(text, context);
                if (audioFile != null) {
                    playAudio(audioFile, context);
                }
            } catch (Exception e) {
                Log.e("ElevenLabsTTS", "Error in TTS process", e);
            }
        });
    }

    private static File fetchAudioFile(String text, Context context) throws IOException {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("text", text);
        } catch (Exception e) {
            Log.e("ElevenLabsTTS", "JSON Error", e);
        }

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("xi-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                // Save the audio file to local storage
                File audioFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "tts_audio.mp3");
                try (InputStream inputStream = response.body().byteStream();
                     FileOutputStream outputStream = new FileOutputStream(audioFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                return audioFile;
            } else {
                Log.e("ElevenLabsTTS", "Error response: " + response.code());
            }
        }
        return null;
    }

    private static void playAudio(File audioFile, Context context) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("ElevenLabsTTS", "Error playing audio", e);
        }
    }
}
