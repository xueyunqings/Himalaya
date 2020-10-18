package com.example.himalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.adapters.DetailListAdapter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.ImageBlur;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.RoundRectImageView;
import com.example.himalaya.views.UILoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener {

    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mTitle;
    private TextView mAuthor;
    private RecyclerView detailListView;
    private AlbumDetailPresenter albumDetailPresenter;
    private DetailListAdapter mDetailListAdapter;
    private UILoader uiLoader;
    private FrameLayout detailListContainer;
    private int mCurrentPage = 1;
    private long mCurrentId = -1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallback(this);
    }

    private void initView() {
        detailListContainer = this.findViewById(R.id.detail_list_container);
        if(uiLoader == null) {
            uiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            detailListContainer.removeAllViews();
            detailListContainer.addView(uiLoader);
            uiLoader.setOnRetryClickListener(DetailActivity.this);
        }
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.viv_small_cover);
        mTitle = this.findViewById(R.id.tv_album_title);
        mAuthor = this.findViewById(R.id.tv_album_author);
    }

    private View createSuccessView(ViewGroup container) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_detail_list,container,false);
        detailListView = view.findViewById(R.id.album_detail_list);
        //RecyclerView的使用步骤
        //1，设置适配管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        detailListView.setLayoutManager(layoutManager);
        //2，设置适配器
        mDetailListAdapter = new DetailListAdapter();
        detailListView.setAdapter(mDetailListAdapter);
        //设置item间距
        detailListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = UIUtil.dip2px(view.getContext(),2);
                outRect.bottom = UIUtil.dip2px(view.getContext(),2);
                outRect.left = UIUtil.dip2px(view.getContext(),2);
                outRect.right = UIUtil.dip2px(view.getContext(),2);
            }
        });
        mDetailListAdapter.setItemClickListener(this);
        return view;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if(tracks == null || tracks.size()==0) {
            if(uiLoader != null){
                uiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
        uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        if(mDetailListAdapter != null){
            mDetailListAdapter.setData(tracks);
        }
    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        mCurrentId = album.getId();

        //拿数据，显示loading状态
        if(uiLoader != null){
            uiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        //网络请求 --> 获取专辑的详细内容
        albumDetailPresenter.getAlbumDetail((int)mCurrentId, mCurrentPage);
        if (mTitle != null) {
            mTitle.setText(album.getAlbumTitle());
        }
        if (mAuthor != null) {
            mAuthor.setText(album.getAnnouncer().getNickname());
        }
        //做毛玻璃效果
        if (mLargeCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                @Override
                public void onSuccess() {
                    ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                }

                @Override
                public void onError() {
                    LogUtil.printI_NORMA("onError");
                }
            });

        }
        if (mSmallCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mSmallCover);
        }
    }

    @Override
    public void onRetryClick() {
        uiLoader.updateStatus(UILoader.UIStatus.LOADING);
        albumDetailPresenter.getAlbumDetail((int)mCurrentId, mCurrentPage);
    }

    @Override
    public void onLoaderMoreFinished(int size) {

    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData,position);
        Intent intent = new Intent(this,PlayerActivity.class);
        startActivity(intent);
    }
}
