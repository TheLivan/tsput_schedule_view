package com.thelivan.televisor;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thelivan.televisor.config.LocalConfig;
import com.thelivan.televisor.config.SiteConfig;
import com.thelivan.televisor.inet.RequestHandler;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private List<SiteConfig> siteConfigs;
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

            LocalConfig.Data defaultData = new LocalConfig.Data("https://thelivan.github.io/config.json");
            LocalConfig.Data data = LocalConfig.load(getBaseContext());
            if (data == null) {
                data = defaultData;
                LocalConfig.save(getBaseContext(), defaultData);
            }

            RequestHandler requestHandler = new RequestHandler();
            try {
                String s = requestHandler.doRequest(data.getConfigLink());
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
            } catch (Exception e) {
                runOnUiThread(() -> {
                    onError(e.getMessage());
                });
            }
        });
        getData.start();
    }

    private void loadingStop() {
        TextView textView = findViewById(R.id.textView);
        textView.setText(null);
    }

    private void onError(String error) {
        TextView textView = findViewById(R.id.textView);
        textView.setText(String.format(getResources().getString(R.string.error_out), error));
    }

    private void init(List<SiteConfig> siteConfigs) {
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setBackgroundColor(Color.parseColor("#3498db"));
        webView.setWebViewClient(new WebViewClient());

        this.siteConfigs = siteConfigs;

        changePage(0);

        int time = this.siteConfigs.get(currentPage).getTime();
        timer.schedule(new SiteTimerTask(), time);
    }

    class SiteTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(() -> changePage(++currentPage % siteConfigs.size()));
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SiteTimerTask.this.run();
                }
            }, nextPage().getTime());
        }
    }

    private void changePage(int index) {
        currentPage = index;
        webView.loadUrl(siteConfigs.get(index).getLink());
    }

    private SiteConfig nextPage() {
        int next = currentPage + 1;
        return siteConfigs.get(next % siteConfigs.size());
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