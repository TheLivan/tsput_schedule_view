package com.thelivan.televisor;

import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
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

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        urls = new ArrayList<>();
        urls.add("https://tsput.ru/");
        urls.add("https://thelivan.github.io/");
        urls.add("https://forum.mcmodding.ru/");

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