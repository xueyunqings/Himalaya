package com.example.himalaya.interfaces;

public interface IBasePresenter<T> {

    /**
     * 注册UI的回调
     */
    void registerViewCallback(T t);

    /**
     * 取消注册UI的回调
     */
    void unRegisterViewCallback(T t);
}
