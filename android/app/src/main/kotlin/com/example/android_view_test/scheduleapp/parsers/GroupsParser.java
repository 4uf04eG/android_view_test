package com.example.android_view_test.scheduleapp.parsers;


import android.content.Context;
import android.os.AsyncTask;

import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.containers.GroupsContainer;
import com.example.android_view_test.scheduleapp.listeners.GroupsAsyncTaskListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

public class GroupsParser extends AsyncTask<Void, Integer, GroupsContainer> {
    private static final String GROUP_LINKS = "group_links";

    private final WeakReference<Context> context;

    public GroupsParser(Context context) {
        this.context = new WeakReference<>(context);
    }

    /**
     * In background gets default links from storage.
     * After that tries to parse those links.
     * Every item in rows and columns checked for emptiness.
     * If it's not empty then adds it to GroupContainer.
     *
     * @return {@link GroupsContainer} with all groups.
     * Could be empty if no urls at storage or couldn't parse link
     */
    @Override
    protected GroupsContainer doInBackground(Void... voids) {
        GroupsContainer groups = new GroupsContainer();
        String[] urls = context.get().getResources().getStringArray(R.array.default_group_links);

        try {
            for (String url : urls) {
                Document doc = Jsoup.parse(new URL(url), 15000);
                Elements rows = doc.select("tr");

                for (Element row : rows) {
                    Elements columns = row.select("td");

                    for (Element column : columns) {
                        if (!column.hasText()) {
                            groups.switchToNextColumn();
                            continue;
                        }

                        if (Character.isDigit(column.text().charAt(0))) {
                            continue;
                        }

                        addGroup(groups, url, column);
                    }

                    groups.switchToNextRow();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return groups;
    }

    /**
     * Stops refreshing of SwipeRefreshLayout using {@link GroupsAsyncTaskListener interface}.
     * After that checks if size of result of operation more than a zero.
     * If result not a zero adds it to view using interface.
     * Otherwise shows toast with error message and view with retry message.
     *
     * @param groupsContainer result of async operation
     */
    @Override
    protected void onPostExecute(GroupsContainer groupsContainer) {
        super.onPostExecute(groupsContainer);
        GroupsAsyncTaskListener listener;

        if (context.get() instanceof GroupsAsyncTaskListener) {
            listener = (GroupsAsyncTaskListener) context.get();
        } else {
            return;
        }

        listener.finishRefreshing();

        if (groupsContainer.size() == 0) {
            listener.showErrorToast();
            listener.showRetryMessage();
        } else {
            listener.addGroupsToView(groupsContainer);
        }
    }

    /**
     * Gets link from href attribute.
     * If it's not contains https, http or www at the start, then adds parent link.
     *
     * @param groups Container for new groups
     * @param url    Link to add to group's if there's no absolute path
     * @param column Contains group name and link
     */
    private void addGroup(GroupsContainer groups, String url, Element column) {
        String link = column.select("a").attr("href");

        if (!link.isEmpty() && !link.matches("^(https?://|www.)")) {
            link = url.substring(0, url.lastIndexOf('/') + 1) + link;
        }

        groups.add(column.text(), link);
    }
}


