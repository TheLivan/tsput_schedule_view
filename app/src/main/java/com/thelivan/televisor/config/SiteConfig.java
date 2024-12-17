package com.thelivan.televisor.config;

public class SiteConfig {
    public String link;
    public int time;

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
