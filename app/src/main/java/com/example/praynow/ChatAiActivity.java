package com.example.praynow;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
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

public class ChatAiActivity extends AppCompatActivity {
    private RecyclerView recyclerChat;
    private EditText edtMessage;
    private ChatAdapter adapter;
    private List<ChatModel> chatList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ai);

        edtMessage = findViewById(R.id.edtMessage);
        recyclerChat = findViewById(R.id.recyclerChat);
        ImageButton btnSend = findViewById(R.id.btnSend);
        ImageView btnBack = findViewById(R.id.btnBack);

        adapter = new ChatAdapter(chatList);
        recyclerChat.setAdapter(adapter);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));

        btnSend.setOnClickListener(v -> {
            String msg = edtMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                tambahChat(msg, true);
                edtMessage.setText("");
                panggilAI(msg);
            }
        });

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void panggilAI(String prompt) {
        tambahChat("AI sedang mengetik...", false);
        final int loadingIndex = chatList.size() - 1;

        String apiKey = BuildConfig.GROQ_API_KEY;

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "llama-3.3-70b-versatile");

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", "Kamu adalah asisten PrayNow yang ramah."));
            messages.put(new JSONObject().put("role", "user").put("content", prompt));

            jsonBody.put("messages", messages);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    updateAIPesan(loadingIndex, "Koneksi Gagal.");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String res = response.body().string();
                            JSONObject jo = new JSONObject(res);
                            String hasil = jo.getJSONArray("choices").getJSONObject(0)
                                    .getJSONObject("message").getString("content");
                            updateAIPesan(loadingIndex, hasil.trim());
                        } catch (Exception e) {
                            updateAIPesan(loadingIndex, "Gagal memproses data.");
                        }
                    } else {
                        updateAIPesan(loadingIndex, "Error Server (" + response.code() + ")");
                    }
                }
            });
        } catch (Exception e) {
            updateAIPesan(loadingIndex, "Gagal menyusun pesan.");
        }
    }

    private void updateAIPesan(int index, String pesanBaru) {
        runOnUiThread(() -> {
            if (index >= 0 && index < chatList.size()) {
                chatList.set(index, new ChatModel(pesanBaru, ChatModel.SENT_BY_BOT));
                adapter.notifyItemChanged(index);
                recyclerChat.scrollToPosition(index);
            }
        });
    }

    private void tambahChat(String msg, boolean isUser) {
        String sender = isUser ? ChatModel.SENT_BY_ME : ChatModel.SENT_BY_BOT;
        chatList.add(new ChatModel(msg, sender));
        adapter.notifyItemInserted(chatList.size() - 1);
        recyclerChat.scrollToPosition(chatList.size() - 1);
    }
}