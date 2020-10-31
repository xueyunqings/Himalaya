package com.example.himalaya.presenters;

import android.util.Log;

import com.example.himalaya.api.XimalayaApi;
import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {

    private final String TAG = "RecommendPresenter";

    private List<IRecommendViewCallback> callbacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;

    private RecommendPresenter() {
    }

    private static RecommendPresenter sInstance = null;

    public static RecommendPresenter getInstance() {
        if(sInstance == null){
            synchronized (RecommendPresenter.class){
                if(sInstance == null){
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    //网络请求获取数据
    @Override
    public void getRecommendList() {

        updateLoading();

        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if(gussLikeAlbumList != null){
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //数据回来则通知
                    handlerRecommendResult(albumList);
                }
            }
            @Override
            public void onError(int i, String s) {
                //数据获取出错
                Log.d(TAG,"error --> "+ i);
                Log.d(TAG,"errorMsg --> "+ s);
                handlerRrror();
            }
        });
    }

    private void handlerRrror() {
        if(callbacks != null){
            for(IRecommendViewCallback callback : callbacks){
                callback.onNetworkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI
        if(albumList !=null){
            if(albumList.size() == 0){
                for(IRecommendViewCallback callback : callbacks){
                    callback.onEmpty();
                }
            }else{
                for(IRecommendViewCallback callback : callbacks){
                    callback.onRecommendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }
        }
    }

    /**
     * 获取当前的推荐专辑列表
     *
     * @return 推荐专辑列表，使用这前要判空
     */
    public List<Album> getCurrentRecommend() {
        return mCurrentRecommend;
    }

    private void updateLoading(){
        for(IRecommendViewCallback callback : callbacks){
            callback.onLoadinng();
        }
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if(callbacks!=null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if(callbacks!=null){
            callbacks.remove(callback);
        }
    }
}
