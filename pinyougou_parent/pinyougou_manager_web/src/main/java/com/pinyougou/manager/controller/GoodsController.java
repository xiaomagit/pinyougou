package com.pinyougou.manager.controller;

import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Autowired
    private Destination queueSolrDestination;//用于发送solr导入的消息

    @Autowired
    private Destination queueSolrDeleteDestination;//用于发送solr删除库中的数据

    @Autowired
    private Destination topicPageDestination;//用于发送生成商品详情页的消息

    @Autowired
    private Destination topicPageDeleteDestination;//用于发送删除商品详情页的消息

    @Autowired
    private JmsTemplate jmsTemplate;

//    @Reference
//    private ItemSearchService itemSearchService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(final Long[] ids) {
        try {
            goodsService.delete(ids);
            //更新索引库
            if (ids != null && ids.length > 0) {
//                itemSearchService.deleteByGoodsIds(ids);
                //发送消息，用于删除索引库中对应的数据
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
                //发送消息，删除商品详情页
                jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
            }
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        return goodsService.findPage(goods, page, rows);
    }

    /**
     * 更新商品状态
     *
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(final Long[] ids, String status) {
        try {
            //如果装填为1审核通过，查询数据
            if ("1".equals(status)) {
                List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);
                if (itemList.size() > 0) {
                    //发送消息，更新索引库
                    final String jsonString = JSON.toJSONString(itemList);
                    jmsTemplate.send(queueSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(jsonString);
                        }
                    });
                    //批量导入数据
//                    itemSearchService.importList(itemList);
                } else {
                    System.out.println("没有数据导入");
                }
            }
            //发送消息，生成静态页
            for (final Long goodsId : ids) {
                jmsTemplate.send(topicPageDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(goodsId + "");
                    }
                });
            }
            //更新商品状态
            goodsService.updateStatus(ids, status);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }

//    @Reference
//    private ItemPageService itemPageService;

    /**
     * 生成静态页
     *
     * @param goodsId
     */
    @RequestMapping("genHtml")
    public void genHtml(Long goodsId) {
        //itemPageService.genIemPageHtml(goodsId);
    }

}
