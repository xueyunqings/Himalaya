package com.example.himalaya.data;

import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class XimalayaApi {

    private XimalayaApi() {
    }

    private static XimalayaApi sXimalayaApi;

    public static XimalayaApi getXimalayaApi() {
        if (sXimalayaApi == null) {
            synchronized (XimalayaApi.class) {
                if (sXimalayaApi == null) {
                    sXimalayaApi = new XimalayaApi();
                }
            }
        }
        return sXimalayaApi;
    }


    /**
     * 获取推荐内容
     *
     * @param callBack
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack) {
        Map<String, String> params = new HashMap<String, String>();
        //这个参数表示一页数据返回多少条
        params.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMAND + "");
        CommonRequest.getGuessLikeAlbum(params, callBack);
    }

    /**
     * 根据专辑的id获取专辑的内容
     *
     * @param callBack
     * @param albumid
     * @param pageIndex
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callBack, long albumid, int pageIndex) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumid + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, callBack);
    }

    /**
     * 根据关键字进行搜索
     *
     * @param keyword
     */
    public void searchByKeyword(String keyword, int page, IDataCallBack<SearchAlbumList> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        map.put(DTransferConstants.PAGE, page + "");
        CommonRequest.getSearchedAlbums(map, callBack);

    }

    /**
     * 获取推荐的热词
     * @param callBack
     */
    public void getHotWords(IDataCallBack<HotWordList> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP,Constants.COUNT_HOT_WORD+"");
        CommonRequest.getHotWords(map,callBack);
    }

    /**
     * 根据关键字获取联想词
     * @param keyword
     * @param callBack
     */
    public void getSuggestWords(String keyword,IDataCallBack<SuggestWords> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY,keyword);
        CommonRequest.getSuggestWord(map,callBack);
    }

}
