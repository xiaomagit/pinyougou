package com.pinyougou.page.service;

public interface ItemPageService {

    /**
     * 生成商品详情页
     * @param goodsId
     * @return
     */
    public boolean genIemPageHtml(Long goodsId);

    /**
     * 删除商品详情页
     * @param goodsIds
     * @return
     */
    public boolean deleteIemPageHtml(Long[] goodsIds);

}
