package com.thelivan.televisor.config;

public class SiteConfig {
    private String link;
    private int time;

    public SiteConfig(String link, int time) {
        this.link = link;
        this.time = time;
    }

    public String getLink() {
        return link;
    }

    public int getTime() {
        return time;
    }
}
