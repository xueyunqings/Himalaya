package com.example.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;

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
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private List<IPlayerCallBack> mIPlayerCallBack = new ArrayList<>();
    private static volatile PlayerPresenter sPlayerPresenter;
    private XmPlayerManager mPlayerManager;
    private Track mcurrTrack;
    private int mCurrentIndex = 0;
    private final SharedPreferences mPlayModSp;

    public static final int PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;
    private boolean mIsReverse = false;

    //sp's key and name
    public static final String PLAY_MODE_SP_NAME = "PlayMod";
    public static final String PLAY_MODE_SP_KEY = "currentPlayMode";
    private int mCurrentProgressPosition = 0;
    private int mProgressDuration = 0;
    private XmPlayListControl.PlayMode mCurrentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

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
        //需要记录当前的播放模式
        mPlayModSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);
    }

    public void setPlayList(List<Track> list, int palyIndex) {
        if (mPlayerManager != null) {
            isPlayListSet = true;
            mPlayerManager.setPlayList(list, palyIndex);
            mcurrTrack = list.get(palyIndex);
            mCurrentIndex = palyIndex;
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
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mCurrentPlayMode = mode;
            mPlayerManager.setPlayMode(mode);
            //通知UI更新播放模式
            for(IPlayerCallBack iPlayerCallback : mIPlayerCallBack) {
                iPlayerCallback.onPlayModeChange(mode);
            }
            //保存到sp里头去。
            SharedPreferences.Editor edit = mPlayModSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY,getIntByPlayMode(mode));
            edit.commit();
        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode) {
        switch(mode) {
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getModeByInt(int index) {
        switch(index) {
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
        }
        return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> lsit = mPlayerManager.getPlayList();
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBack) {
                iPlayerCallBack.onListLoaded(lsit);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        if(mPlayerManager != null){
            mPlayerManager.play(index);
        }
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
        //把播放列表翻转
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReverse = !mIsReverse;

        //第一个参数是播放列表,第二个参数是开始播放的下标
        //新的下标 = 总的内容个数-1 – 当前的下标
        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        mPlayerManager.setPlayList(playList,mCurrentIndex);
        //更UI
        mcurrTrack = (Track) mPlayerManager.getCurrSound();
        for(IPlayerCallBack iPlayerCallback : mIPlayerCallBack) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdate(mcurrTrack,mCurrentIndex);
            iPlayerCallback.updateListOrder(mIsReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {

    }

    @Override
    public void registerViewCallback(IPlayerCallBack iPlayerCallBack) {
        iPlayerCallBack.onTrackUpdate(mcurrTrack,mCurrentIndex);
        //从sp里头拿
        int modeIndex = mPlayModSp.getInt(PLAY_MODE_SP_KEY,PLAY_MODEL_LIST_INT);
        mCurrentPlayMode = getModeByInt(modeIndex);
        iPlayerCallBack.onPlayModeChange(getModeByInt(modeIndex));


        if(!mIPlayerCallBack.contains(iPlayerCallBack)){
            mIPlayerCallBack.add(iPlayerCallBack);
        }
    }

    /**
     * 判断是否有播放有播放的节目列表
     *
     * @return
     */
    public boolean hasPlayList() {
        return isPlayListSet;
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
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if(mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            //播放器准备完了，可以去播放了
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if(curModel instanceof Track){
            Track track = (Track) curModel;
            mcurrTrack = track;
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBack) {
                iPlayerCallBack.onTrackUpdate(mcurrTrack,mCurrentIndex);
            }
        }
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
