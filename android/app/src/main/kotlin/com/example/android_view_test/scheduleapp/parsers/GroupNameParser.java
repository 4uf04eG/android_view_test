package com.example.android_view_test.scheduleapp.parsers;

import android.content.Context;
import android.os.AsyncTask;

import com.example.android_view_test.scheduleapp.helpers.StorageHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupNameParser extends AsyncTask<Context, Integer, String> {
    private static final String SCHEDULE_LINK = "schedule_link";

    @Override
    protected String doInBackground(Context... contexts) {
        String link = StorageHelper.findStringInShared(contexts[0], SCHEDULE_LINK);

        try {
            Document doc = Jsoup.parse(new URL(link), 15000);
            return getNameFromDoc(doc);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    static String getNameFromDoc(Document doc) {
        String raw = doc.select("p > font").get(1).text();
        Matcher matcher = Pattern.compile("([\\w-]+(, [\\w-]+)?) [\\w-]+").matcher(raw);

        if (matcher.find()) return matcher.group(1);
        else return "";
    }
}
