package com.example.himalaya.presenters;

import com.example.himalaya.data.XimalayaApi;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.interfaces.ISearchPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    List<ISearchCallback> mCallbackList = new ArrayList<>();
    private String mCurrentKeyword = null;
    private XimalayaApi mXimalayaApi = null;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = 1;
    private List<Album> mSearchResult = new ArrayList<>();
    private boolean mIsLoadMore = false;

    private SearchPresenter(){
        mXimalayaApi = XimalayaApi.getXimalayaApi();
    }

    private static SearchPresenter sSearchPresenter = null;

    public static SearchPresenter getSearchPresenter() {
        if(sSearchPresenter == null){
            synchronized (SearchPresenter.class){
                if(sSearchPresenter == null){
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }


    @Override
    public void doSearch(String keyWord) {
        mCurrentPage = DEFAULT_PAGE;
        mSearchResult.clear();
        //保存keyWord，当网络不好时，用于重新搜索
        this.mCurrentKeyword = keyWord;
        search(keyWord);
    }

    private void search(String keyword) {
        mXimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                mSearchResult.addAll(albums);
                if (albums != null) {
                    if(mIsLoadMore) {
                        for (ISearchCallback iSearchCallback : mCallbackList) {
                            iSearchCallback.onLoadMoreResult(mSearchResult,albums.size()!=0);
                        }
                        mIsLoadMore = false;
                    } else {
                        for (ISearchCallback iSearchCallback : mCallbackList) {
                            iSearchCallback.onSearchResultLoaded(mSearchResult);
                        }
                    }

                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.printI_NORMAL("search --> errorCode:"+errorCode+" , errorMsg:"+errorMsg);
                for (ISearchCallback iSearchCallback : mCallbackList) {
                    if (mIsLoadMore) {
                        iSearchCallback.onLoadMoreResult(mSearchResult, false);
                        mCurrentPage--;
                        mIsLoadMore = false;
                    } else {
                        iSearchCallback.onError(errorCode, errorMsg);
                    }
                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    @Override
    public void loadMore() {
        //判断有没有必要进行加载更多
        if (mSearchResult.size() < Constants.COUNT_DEFAULT) {
            for (ISearchCallback iSearchCallback : mCallbackList) {
                iSearchCallback.onLoadMoreResult(mSearchResult, false);
            }
        } else {
            mIsLoadMore = true;
            mCurrentPage++;
            search(mCurrentKeyword);
        }
    }

    @Override
    public void getHotWord() {
        //todo: 做一个缓存
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if(hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    for (ISearchCallback iSearchCallback : mCallbackList) {
                        iSearchCallback.onHotWordLoaded(hotWords);
                    }
                }
            }
            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.printI_NORMAL("getHotWord --> errorCode:"+errorCode+" , errorMsg:"+errorMsg);
            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
        mXimalayaApi.getSuggestWords(keyword, new IDataCallBack<SuggestWords>(){
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if(suggestWords != null) {
                    List<QueryResult> suggestWordList = suggestWords.getKeyWordList();
                    for (ISearchCallback iSearchCallback : mCallbackList) {
                        iSearchCallback.onRecommendWordLoaded(suggestWordList);
                    }
                }
            }
            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.printI_NORMAL("getRecommendWord --> errorCode:"+errorCode+" , errorMsg:"+errorMsg);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbackList.contains(iSearchCallback)) {
            mCallbackList.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        mCallbackList.remove(iSearchCallback);
    }
}
