package com.example.himalaya.presenters;

import com.example.himalaya.data.XimalayaApi;
import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();

    private static AlbumDetailPresenter sInstance = null;
    private List<Track> mTracks = new ArrayList<>();
    private Album mTargetAlbum = null;
    private int mCurrentAlbumId = -1;
    private int mCurrentPage = 0;

    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
        //去加载更多内容
        mCurrentPage++;
        //传入true，表示结果会追加到列表的后方。
        doLoaded(true);
    }

    private void doLoaded(final boolean isLoaderMore) {
        XimalayaApi xXimalayaApi = XimalayaApi.getXimalayaApi();
        xXimalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    if (isLoaderMore) {
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoaderMoreResult(size);
                    } else {
                        mTracks.addAll(0, tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isLoaderMore) {
                    mCurrentPage--;
                }
                LogUtil.printI_NORMAL("errorCode --> " + errorCode);
                LogUtil.printI_NORMAL("errorMsg --> " + errorMsg);
                handlerError(errorCode, errorMsg);
            }
        }, mCurrentAlbumId, mCurrentPage);
    }

    /**
     * 处理加载更多的结果
     *
     * @param size
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPage = page;
        doLoaded(false);
    }

    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onNetworkError(errorCode, errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
            if (mTargetAlbum != null) {
                callback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback callback) {
        mCallbacks.remove(callback);
    }

    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }
}
