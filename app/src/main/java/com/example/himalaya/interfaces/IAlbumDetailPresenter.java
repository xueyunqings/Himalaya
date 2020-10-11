package com.example.himalaya.interfaces;


/**
 * Created by TrillGates on 2010/10/11.
 */
public interface IAlbumDetailPresenter {

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上接加载更多
     */
    void loadMore();


    /**
     * 获取专辑详情
     *
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId, int page);

    void registerViewCallback(IAlbumDetailViewCallback callback);

    void unRegisterViewCallback(IAlbumDetailViewCallback callback);

}
