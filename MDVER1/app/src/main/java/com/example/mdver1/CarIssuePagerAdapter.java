package com.example.mdver1;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class CarIssuePagerAdapter extends PagerAdapter {

    private int mSize;

    public CarIssuePagerAdapter() {
        mSize = 5;
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup view, int position, @NonNull Object object) {
        view.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {

        ImageView imageView = new ImageView(view.getContext());
        int[] issueImage = new int[]{
                R.drawable.car_issue_list,
                R.drawable.car_issue_list,
                R.drawable.car_issue_list,
                R.drawable.car_issue_list,
                R.drawable.car_issue_list
        };
        imageView.setImageResource(issueImage[position]);
        view.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return imageView;
    }

}