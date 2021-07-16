package com.example.android_view_test.scheduleapp.parsers;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android_view_test.scheduleapp.containers.ScheduleContainer;
import com.example.android_view_test.scheduleapp.helpers.StorageHelper;
import com.example.android_view_test.scheduleapp.listeners.ScheduleAsyncTaskListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Locale;

public class ScheduleParser extends AsyncTask<Boolean, Integer, ScheduleContainer> {
    private static final String SCHEDULE_LINK = "schedule_link";
    private static final String GROUP_NAME = "group_name";

    private static boolean isGroupLinkChanged;
    private static boolean isRetryAfterLinkChanged;

    private final ScheduleAsyncTaskListener listener;
    private final WeakReference<Context> context;

    public ScheduleParser(@NonNull ScheduleAsyncTaskListener listener) {
        this.listener = listener;
        context = new WeakReference<>(((Fragment) listener).requireContext());
    }

    public ScheduleParser(@NonNull Context context) {
        listener = null;
        this.context = new WeakReference<>(context);
    }

    /**
     * It gets schedule from specified link located in storage.
     * Link being converted to url and parsed.
     * If site couldn't be parsed or not reachable then it returns empty {@link ScheduleContainer}.
     * Otherwise it checks every table, row and column if the inner text is empty or starts with
     * "пр.", "лек." or "лаб.". That way it's possible to separate cells containing information like
     * call schedule and days of week from cells with schedule.
     * If it matches that regex then class count is increased and if it's not empty
     * string containing current class with count added to container.
     *
     * Switching days in container occurs only if class count is more than 5.
     * It's needed to ignore rows containing empty cells which are not part of a schedule
     *
     * @return contains schedule grouped by weeks and days
     */
    @Override
    protected ScheduleContainer doInBackground(Boolean... booleans) {
        Elements tables = null;
        ScheduleContainer schedule = new ScheduleContainer();
        String link = StorageHelper.findStringInShared(context.get(), SCHEDULE_LINK);

        if (booleans.length > 0) isRetryAfterLinkChanged = booleans[0];

        try {
            Document doc = Jsoup.parse(new URL(link), 15000);

            String expectedName = StorageHelper.findStringInShared(context.get(), GROUP_NAME);
            String actualName = GroupNameParser.getNameFromDoc(doc);

            if (expectedName != null && !expectedName.equals(actualName)) isGroupLinkChanged = true;
            else tables = doc.select("table");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        if (tables == null) return schedule;

        for (Element week : tables) {
            for (Element day : week.select("tr")) {
                int periodsCount = 0;

                for (Element period : day.select("td")) {
                    String text = period.text();

                    if (text.toLowerCase().matches("^(пр.|лек.|лаб.|$).*")) {
                        periodsCount++;

                        if (!text.isEmpty()) {
                            schedule.add(String.format(Locale.US, "%d) %s", periodsCount, text));
                        }
                    }
                }

                if (periodsCount > 5) {
                    schedule.switchToNextDay();
                }
            }

            schedule.switchToNextWeek();
        }

        return schedule;
    }

    /**
     * @param scheduleContainer result of async operation
     */
    @Override
    protected void onPostExecute(ScheduleContainer scheduleContainer) {
        super.onPostExecute(scheduleContainer);
        if (listener == null) return;

        if (scheduleContainer.size() != 0) {
            listener.finishRefreshing();

            if (!StorageHelper.isScheduleChanged(context.get(), scheduleContainer)) return;

            listener.addScheduleToView(scheduleContainer);
            listener.storeSchedule(context.get(), scheduleContainer);
        } else if (isGroupLinkChanged && !isRetryAfterLinkChanged) {
            listener.reloadSchedule();
        } else {
            listener.finishRefreshing();
            listener.showErrorToast(context.get());
        }
    }
}
