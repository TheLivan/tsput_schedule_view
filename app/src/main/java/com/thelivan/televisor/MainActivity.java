package com.thelivan.televisor;

import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thelivan.televisor.config.SiteConfig;
import com.thelivan.televisor.inet.RequestHandler;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private List<String> urls;
    private static final int DELAY = 30_000;
    private final Timer timer = new Timer();
    private final Handler handler = new Handler();
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        loading();
    }

    private void loading() {
        TextView textView = findViewById(R.id.textView);
        textView.setText(getResources().getString(R.string.loading));
        loadConfig();
    }

    private void loadConfig() {
        Thread getData = new Thread(() -> {
            RequestHandler requestHandler = new RequestHandler();
            TextView textView = findViewById(R.id.textView);
            try {
                String s = requestHandler.doRequest("https://thelivan.github.io/config.json");
                List<SiteConfig> sites = new ArrayList<>();

                JsonParser parser = new JsonParser();
                JsonObject object = (JsonObject) parser.parse(new StringReader(s));
                JsonArray array = object.get("sites").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    JsonObject o1 = array.get(i).getAsJsonObject();
                    sites.add(new SiteConfig(o1.get("link").getAsString(), o1.get("time").getAsInt()));
                }
                runOnUiThread(() -> {
                    init(sites);
                    loadingStop();
                });
            } catch (Throwable e) {
                textView.setText(String.format(getResources().getString(R.string.error_out), e.getMessage()));
            }
        });
        getData.start();
    }

    private void loadingStop() {
        TextView textView = findViewById(R.id.textView);
        textView.setText(null);
    }

    private void onError() {
        TextView textView = findViewById(R.id.textView);
        textView.setText(getResources().getString(R.string.error_text));
    }

    private void init(List<SiteConfig> siteConfigs) {
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        urls = new ArrayList<>();
        siteConfigs.stream().forEach(x -> urls.add(x.getLink()));

        changePage(0);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> changePage(++currentPage % urls.size()));
            }
        };
        timer.schedule(task, DELAY, DELAY);
    }

    private void changePage(int index) {
        currentPage = index;
        webView.loadUrl(urls.get(index));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}