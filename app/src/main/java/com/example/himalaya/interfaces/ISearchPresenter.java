package com.example.himalaya.interfaces;

public interface ISearchPresenter extends IBasePresenter<ISearchCallback> {

    void doSearch(String keyWord);

    void reSearch();

    void loadMore();

    void getHotWord();

    void getRecommendWord(String keyword);
}
