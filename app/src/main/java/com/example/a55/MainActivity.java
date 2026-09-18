package com.example.a55;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    RecyclerView chatRecycler;
    EditText t;
    Button b;

    List<Message> messageList;
    ChatAdapter adapter;

    OkHttpClient client;

    // ✅ PUT YOUR GROQ API KEY HERE
    String GROQ_API_KEY = "";

    // ✅ GROQ API URL (OpenAI compatible)
    String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatRecycler = findViewById(R.id.chatRecycler);
        t = findViewById(R.id.t);
        b = findViewById(R.id.b);

        client = new OkHttpClient();
        messageList = new ArrayList<>();
        adapter = new ChatAdapter(messageList);

        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(adapter);

        b.setOnClickListener(v -> {

            String userMsg = t.getText().toString().trim();

            if (userMsg.isEmpty())
                return;

            addMessage(userMsg, true);

            t.setText("");

            callAI(userMsg);
        });
    }

    void addMessage(String text, boolean isUser) {

        runOnUiThread(() -> {

            messageList.add(new Message(text, isUser));

            adapter.notifyItemInserted(messageList.size() - 1);

            chatRecycler.scrollToPosition(messageList.size() - 1);
        });
    }

    void callAI(String userMessage) {

        try {

            JSONObject json = new JSONObject();

            // ✅ GROQ MODEL (FREE)
            json.put("model", "llama-3.3-70b-versatile");

            JSONArray messages = new JSONArray();

            JSONObject userObj = new JSONObject();
            userObj.put("role", "user");
            userObj.put("content", userMessage);

            messages.put(userObj);

            json.put("messages", messages);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            // ✅ GROQ AUTH HEADER
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + GROQ_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                    addMessage("Connection Error: " + e.getMessage(), false);

                    Log.e("GROQ_ERROR", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseBody = response.body().string();

                    Log.d("GROQ_RESPONSE", responseBody);

                    if (!response.isSuccessful()) {

                        addMessage("API Error: " + responseBody, false);
                        return;
                    }

                    try {

                        JSONObject obj = new JSONObject(responseBody);

                        String reply = obj
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        addMessage(reply, false);

                    } catch (Exception e) {

                        addMessage("Parsing Error: " + e.getMessage(), false);

                        Log.e("PARSE_ERROR", e.getMessage());
                    }
                }
            });

        } catch (Exception e) {

            addMessage("Request Error: " + e.getMessage(), false);

            Log.e("REQUEST_ERROR", e.getMessage());
        }
    }
}