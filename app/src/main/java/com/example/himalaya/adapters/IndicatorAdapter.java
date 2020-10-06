package com.example.himalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.himalaya.MainActivity;
import com.example.himalaya.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

public class IndicatorAdapter extends CommonNavigatorAdapter {

    private final String[] mTitles;
    private OnIndicatorTapClickListener mOnIndicatorTapClickListener;

    public IndicatorAdapter(Context context) {
        mTitles = context.getResources().getStringArray(R.array.indicator_title);
    }

    @Override
    public int getCount() {
        if(mTitles!=null){
            return mTitles.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        //创建view
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
        //设置一般情况下的颜色为灰色
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        //单位sp
        colorTransitionPagerTitleView.setText(mTitles[index]);
        //设置title的点击事件，这里的话，如果点击了title，就选中对用的viewPager
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnIndicatorTapClickListener != null){
                    mOnIndicatorTapClickListener.onTabClick(index);
                }
            }
        });
        return colorTransitionPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        indicator.setColors(Color.parseColor("#ffffff"));
        return indicator;
    }

    public void setOnIndicatorTapClickListener(OnIndicatorTapClickListener listener){
        this.mOnIndicatorTapClickListener = listener;
    }

    public interface  OnIndicatorTapClickListener {
        void onTabClick(int index);
    }
}
