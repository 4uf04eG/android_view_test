package com.example.android_view_test.scheduleapp.adapters;

import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_view_test.R;
import com.example.android_view_test.scheduleapp.containers.ClassData;

import java.util.List;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {
    private static final int SCHEDULE_IS_EMPTY = 0;
    private static final int SCHEDULE_IS_NOT_EMPTY = 1;

    private List<ClassData> schedule;

    public ScheduleListAdapter(List<ClassData> schedule) {
        this.schedule = schedule;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == SCHEDULE_IS_NOT_EMPTY) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_schedule, viewGroup, false));
        }

        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_empty_schedule, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == SCHEDULE_IS_EMPTY) return;

        ClassData classInfo = schedule.get(position);
        Resources resources = viewHolder.itemView.getResources();

        String[] teachers = classInfo.getTeachers();
        String[] rooms = classInfo.getClassRooms();
        String[] names = classInfo.getNames();

        viewHolder.title.setText(names[0]);
        viewHolder.time.setText(getTimeByClassPosition(resources, classInfo.getPosition()));
        viewHolder.type.setText(getFullType(resources, classInfo.getType(),
                viewHolder.typeContainer));
        viewHolder.classRoom.setText(rooms[0]);

        if (teachers[0] != null) {
            viewHolder.teacher.setVisibility(View.VISIBLE);
            viewHolder.teacher.setText(fixInitials(teachers[0]));
        } else {
            viewHolder.teacher.setVisibility(View.GONE);
        }

        viewHolder.secondTitle.setVisibility(View.GONE);
        viewHolder.divider.setVisibility(View.GONE);
        viewHolder.secondTeacher.setVisibility(View.GONE);
        viewHolder.secondClassRoom.setVisibility(View.GONE);

        if (teachers[1] != null) {
            if (names[1] != null) {
                viewHolder.secondTitle.setVisibility(View.VISIBLE);
                viewHolder.secondTitle.setText(names[1]);
            }

            viewHolder.divider.setVisibility(View.VISIBLE);
            viewHolder.secondTeacher.setVisibility(View.VISIBLE);
            viewHolder.secondTeacher.setText(fixInitials(teachers[1]));
            viewHolder.secondClassRoom.setVisibility(View.VISIBLE);
            viewHolder.secondClassRoom.setText(rooms[1]);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return schedule.isEmpty() ? SCHEDULE_IS_EMPTY : SCHEDULE_IS_NOT_EMPTY;
    }

    @Override
    public int getItemCount() {
        return schedule.isEmpty() ? 1 : schedule.size();
    }

    public void refreshData(List<ClassData> newData) {
        if (newData != null) {
            schedule = newData;
            notifyDataSetChanged();
        }
    }

    private String getFullType(Resources resources,
                               String shortTypeStr,
                               LinearLayout typeContainer) {
        if (shortTypeStr == null) return "";

        shortTypeStr = shortTypeStr.toLowerCase();

        switch (shortTypeStr) {
            case "пр":
                typeContainer.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light));
                return resources.getString(R.string.schedule_types_practice);
            case "лаб":
                typeContainer.setBackgroundColor(resources.getColor(android.R.color.holo_green_light));
                return resources.getString(R.string.schedule_types_lab_work);
            case "лек":
                typeContainer.setBackgroundColor(resources.getColor(android.R.color.holo_orange_light));
                return resources.getString(R.string.schedule_types_lecture);
            default:
                typeContainer.setBackgroundColor(resources.getColor(android.R.color.holo_red_light));
                return "";
        }
    }

    private Spanned getTimeByClassPosition(Resources resources, String classPos) {
        String[] times = resources.getStringArray(R.array.class_times);
        int index = Integer.parseInt(classPos) - 1;

        if (index >= 0 && index < times.length) return Html.fromHtml(times[index]);
        else return Html.fromHtml("<b>--</b><br/>--");
    }

    private String fixInitials(String teacher) {
        return teacher.replaceAll("(\\b\\w\\b)", "$1.");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout typeContainer;
        private final View divider;
        private final TextView time;
        private final TextView type;
        private final TextView title;
        private final TextView teacher;
        private final TextView classRoom;
        private final TextView secondTitle;
        private final TextView secondTeacher;
        private final TextView secondClassRoom;

        ViewHolder(final View itemView) {
            super(itemView);
            typeContainer = itemView.findViewById(R.id.schedule_item_type_container);
            divider = itemView.findViewById(R.id.schedule_item_divider);
            time = itemView.findViewById(R.id.schedule_item_time);
            type = itemView.findViewById(R.id.schedule_item_type);
            title = itemView.findViewById(R.id.schedule_item_title);
            teacher = itemView.findViewById(R.id.schedule_item_teacher);
            classRoom = itemView.findViewById(R.id.schedule_item_class_room);
            secondTitle = itemView.findViewById(R.id.schedule_item_2nd_title);
            secondTeacher = itemView.findViewById(R.id.schedule_item_2nd_teacher);
            secondClassRoom = itemView.findViewById(R.id.schedule_item_2nd_class_room);
        }
    }
}
