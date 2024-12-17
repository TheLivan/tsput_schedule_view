package com.thelivan.televisor.config;

import android.content.Context;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class LocalConfig {
    private static final String FILE_NAME = "localConfig.json";

    public static boolean save(Context context, Data dataItems) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(dataItems);
        try (FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Data load(Context context) {
        try (FileInputStream fileInputStream = context.openFileInput(FILE_NAME);
                InputStreamReader streamReader = new InputStreamReader(fileInputStream)) {
            Gson gson = new Gson();
            Data dataItems = gson.fromJson(streamReader, Data.class);
            return dataItems;
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static class Data {
        String configLink;
        List<SiteConfig> siteConfigList;

        public Data() {
        }

        public Data(String configLink, List<SiteConfig> siteConfigList) {
            this.configLink = configLink;
            this.siteConfigList = siteConfigList;
        }

        public String getConfigLink() {
            return configLink;
        }

        public List<SiteConfig> getSiteConfigList() {
            return siteConfigList;
        }
    }
}
