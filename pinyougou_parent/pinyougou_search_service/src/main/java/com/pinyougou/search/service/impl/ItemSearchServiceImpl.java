package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 在solr中通过关键字查询相关信息
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {
        //替换空字符串
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));
        Map<String, Object> map = new HashMap<String, Object>();
        //查询列表(高亮显示)
        map.putAll(searchList(searchMap));
        //查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //查询品牌和规格列表
        if (categoryList.size() > 0) {
            map.putAll(searchBrandAndSpecList(categoryList.get(0)));
        }

        return map;
    }

    /**
     * 根据关键字搜索，高亮显示
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        /*//构建query对象
        Query query = new SimpleQuery();
        //查询条件
        Criteria criteria =  new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //查询信息，默认是十条记录
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);*/

        //构建query对象
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);//设置高亮选项

        //1.1设置查询关键字
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //1.2按分类筛选
        if (!"".equals(searchMap.get("category"))) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3按品牌分类筛选
        if (!"".equals(searchMap.get("brand"))) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4按规格信息进行筛选
        if (searchMap.get("spec") != null) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Map<String, Object> specMap = (Map<String, Object>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(searchMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }
        //1.5按照价格查询
        if (!searchMap.get("price").equals("")) {
            String[] price = ((String) searchMap.get("price")).split("-");
            //如果价格区间最低值不等于0
            if (!price[0].equals("0")) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            //如果价格区间最高值不是*
            if (!price[1].equals("*")) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.6分页查询
        //当前页码
        System.out.println(searchMap.get("pageNo"));
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo = 1;
        }
        //当前每页现实的条数
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 20;
        }
        //起始位置
        query.setOffset((pageNo - 1) * pageSize);
        //每页记录数
        query.setRows(pageSize);

        //1.7排序
        String sortValue = (String) searchMap.get("sort");//排序方式
        String sortField = (String) searchMap.get("sortField");//排序字段
        if (!"".equals(sortField) && sortValue != null) {
            //升序
            if (sortValue.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            //降序
            if (sortValue.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }

        }


        //高亮查询
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
            //获得原实体类
            TbItem item = highlightEntry.getEntity();
            //遍历获得高亮显示部分的数据
            /*List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();
            for (HighlightEntry.Highlight highlight : highlightList) {
                List<String> snipplets = highlight.getSnipplets();
                System.out.println(snipplets.get(0));
            }*/
            //判断是否有数据
            if (highlightEntry.getHighlights().size() > 0 && highlightEntry.getHighlights().get(0).getSnipplets().size() > 0) {
                item.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));//设置高亮显示的结果
            }
        }

        List<TbItem> itemList = page.getContent();
        map.put("rows", itemList);
        map.put("totalPages", page.getTotalPages());//总页数
        map.put("total", page.getTotalElements());//总记录数
        return map;
    }

    /**
     * 查询分类列表
     *
     * @param searchMap
     * @return
     */
    private List<String> searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<String>();
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        //按照关键字查询
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据分组页得到分页集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //根据分页集得到分页结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //遍历获得相应的分页结果
        for (GroupEntry<TbItem> groupEntry : content) {
            list.add(groupEntry.getGroupValue());
        }
        return list;
    }

    /**
     * 查询品牌和规格列表
     *
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获得模板id
        if (typeId != null) {
            //根据模板id查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);

            //根据模板id查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }

        return map;
    }

    /**
     * 增量更新索引库
     *
     * @param list
     */
    @Override
    public void importList(List list) {
       /* if (list != null && list.size() > 0) {
        }*/
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }


    /***
     * 根据id删除索引库数据
     * @param goodIds
     */
    @Override
    public void deleteByGoodsIds(Long[] goodIds) {
        SolrDataQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
