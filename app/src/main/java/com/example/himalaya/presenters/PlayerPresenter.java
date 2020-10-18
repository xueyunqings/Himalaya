package com.example.himalaya.presenters;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallBack;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private List<IPlayerCallBack> mIPlayerCallBack = new ArrayList<>();
    private static volatile PlayerPresenter sPlayerPresenter;
    private XmPlayerManager mPlayerManager;

    private boolean isPlayListSet = false;

    public static PlayerPresenter getPlayerPresenter() {
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                if (sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //注册广告相关的接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器相关的接口
        mPlayerManager.addPlayerStatusListener(this);
    }

    public void setPlayList(List<Track> list, int palyIndex) {
        if (mPlayerManager != null) {
            isPlayListSet = true;
            mPlayerManager.setPlayList(list, palyIndex);
        } else {

        }
    }

    @Override
    public void play() {
        if (isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {
        if (mPlayerManager != null) {
            mPlayerManager.stop();
        }
    }

    @Override
    public void playPre() {

    }

    @Override
    public void playNext() {

    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {

    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTo(int progress) {
        //更新播放器的进度
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        return mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {

    }

    @Override
    public void playByAlbumId(long id) {

    }

    @Override
    public void registerViewCallback(IPlayerCallBack iPlayerCallBack) {
        if(!mIPlayerCallBack.contains(iPlayerCallBack)){
            mIPlayerCallBack.add(iPlayerCallBack);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallBack iPlayerCallBack) {
        mIPlayerCallBack.remove(iPlayerCallBack);
    }

    @Override
    public void onStartGetAdsInfo() {
        LogUtil.printI_TEST("onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.printI_TEST("onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.printI_TEST("onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.printI_TEST("onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.printI_TEST("onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.printI_TEST("onCompletePlayAds");
    }

    @Override
    public void onError(int what, int ertra) {
        LogUtil.printI_TEST("onError");
    }

    @Override
    public void onPlayStart() {
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBack) {
            iPlayerCallBack.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBack) {
            iPlayerCallBack.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBack) {
            iPlayerCallBack.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {

    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {

    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int i) {

    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        //播放进度
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBack) {
            iPlayerCallBack.onProgressChange(currPos,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }
}
