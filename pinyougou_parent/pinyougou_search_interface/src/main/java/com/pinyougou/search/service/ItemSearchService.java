package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 从solr中查相关的值
     *
     * @param searchMap
     * @return
     */
    public Map<String, Object> search(Map searchMap);

    /**
     * 增量更新solr索引库
     *
     * @param list
     */
    public void importList(List list);

    /**
     * 删除商品，对索引库进行更新
     *
     * @param goodIds
     */
    public void deleteByGoodsIds(Long[] goodIds);

}
