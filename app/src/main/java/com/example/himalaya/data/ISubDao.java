package com.example.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubDao {

    /**
     * 添加专辑订阅
     *
     * @param album
     */
    void addAlbum(Album album);

    /**
     * 删除订阅内容
     *
     * @param album
     */
    void delAlbum(Album album);


    /**
     * 获取订阅内容
     */
    void listAlbums();

    void setCallback(ISubDaoCallback callback);
}
