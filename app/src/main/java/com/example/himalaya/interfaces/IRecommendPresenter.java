package com.example.himalaya.interfaces;

public interface IRecommendPresenter {

    /**
     * 获取推荐内容
     */
    void  getRecommendList();

    /**
     * 下拉刷新
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 注册UI的回调
     * @param callback
     */
    void registerViewCallback(IRecommendViewCallback callback);

    /**
     * 取消注册UI的回调
     * @param callback
     */
    void unRegisterViewCallback(IRecommendViewCallback callback);
}
