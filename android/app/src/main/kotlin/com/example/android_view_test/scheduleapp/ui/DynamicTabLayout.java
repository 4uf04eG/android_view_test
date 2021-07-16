package com.example.android_view_test.scheduleapp.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class DynamicTabLayout extends TabLayout {

    public DynamicTabLayout(Context context) {
        super(context);
    }

    public DynamicTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup tabLayout = (ViewGroup) getChildAt(0);
        int childCount = tabLayout.getChildCount();

        if (childCount != 0) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int tabMinWidth = displayMetrics.widthPixels / childCount;

            for (int i = 0; i < childCount; i++)
                tabLayout.getChildAt(i).setMinimumWidth(tabMinWidth);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
