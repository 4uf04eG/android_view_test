package com.example.android_view_test.scheduleapp.containers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassData implements Parcelable {
    public static final Creator<ClassData> CREATOR = new Creator<ClassData>() {
        @Override
        public ClassData createFromParcel(Parcel in) {
            return new ClassData(in);
        }

        @Override
        public ClassData[] newArray(int size) {
            return new ClassData[size];
        }
    };
    private final String position;
    private final String type;
    private final String[] names;
    private final String[] teachers;
    private final String[] classRooms;

    ClassData(String classRaw) {
        Pattern pattern = Pattern.compile(Patterns.generateUnitedPattern());
        Matcher matcher = pattern.matcher(classRaw.replaceAll(" +", " "));

        if (matcher.find()) {
            position = matcher.group(1);
            type = matcher.group(2);
            names = new String[]{matcher.group(3), matcher.group(6)};
            teachers = new String[]{matcher.group(4), matcher.group(7)};
            classRooms = new String[]{matcher.group(5), matcher.group(8)};
        } else {
            Log.e("ParsingString", "String could not be parsed: " + classRaw);
            position = "-1";
            type = "";
            names = new String[]{"Parsing Error", null};
            teachers = new String[]{null, null};
            classRooms = new String[]{"Please open online", null};
        }
    }

    private ClassData(Parcel in) {
        position = in.readString();
        type = in.readString();
        names = in.createStringArray();
        teachers = in.createStringArray();
        classRooms = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(position);
        dest.writeString(type);
        dest.writeStringArray(names);
        dest.writeStringArray(teachers);
        dest.writeStringArray(classRooms);
    }

    public String getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public String[] getNames() {
        return names;
    }

    public String[] getTeachers() {
        return teachers;
    }

    public String[] getClassRooms() {
        return classRooms;
    }
}

class Patterns {
    private static final String position = "(\\d)+\\)\\s"; //Example: 2)
    private static final String type = "(\\p{L}+)."; //Example: пр.
    private static final String name = "([-\\p{L}\\d.,:\"()/ ]+?)\\s?"; //Example: Физика - 1 п/г
    private static final String teacher = "([\\p{L}\\d]+ \\p{L} \\p{L}|(?<=\\s)\\p{L}{2,} \\p{L}{2,}|ПрепАДП)?"; //Example: Петров П П
    private static final String classRoom = "\\s([\\d.]+\\s?[-_]\\s?[\\p{L}\\d/.]+|\\+)"; //Example:
    // 2-202
    private static final String secondName = "(?:(?:\\s([-\\p{L}/.,:\"()/ ]+?))?\\s";
    private static final String secondClassRoom = classRoom + ")?";

    static String generateUnitedPattern() {
        if (false) {
            Log.d("regex", position + type + name + teacher + classRoom +
                    secondName + teacher + secondClassRoom);
        }

        return position + type + name + teacher + classRoom +
                secondName + teacher + secondClassRoom;
    }
}

