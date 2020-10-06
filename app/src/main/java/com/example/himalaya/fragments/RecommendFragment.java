package com.example.himalaya.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.RecommendPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {
    private final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView recommendRV;
    private RecommendListAdapter recommendListAdapter;
    private RecommendPresenter mRecommendPresenter;

    @Override
    protected View onSubviewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend,container,false);

        //RecyclerViewd的使用
        //1，找到对应给的控件
        recommendRV = mRootView.findViewById(R.id.recommend_list);
        //2，设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recommendRV.setLayoutManager(linearLayoutManager);
        recommendRV.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        //3，设置适配器
        recommendListAdapter = new RecommendListAdapter();
        recommendRV.setAdapter(recommendListAdapter);

        //获得逻辑层对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();
        return mRootView;
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //数据回来，更新UI
        recommendListAdapter.setData(result);
    }

    @Override
    public void onLoadMore(List<Album> result) {

    }

    @Override
    public void onRefershMore(List<Album> result) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册，避免造成内存泄漏
        if(mRecommendPresenter != null){
            mRecommendPresenter.unRegisterViewCallback(this);
        }

    }
}
