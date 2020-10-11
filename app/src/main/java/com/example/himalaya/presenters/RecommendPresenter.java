package com.example.himalaya.presenters;

import android.util.Log;

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

        //封装参数
        Map<String, String> map = new HashMap<String, String>();
        //这个参数表示一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMAND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
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
            }
        }
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
