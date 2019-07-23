package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

/**
 * 品牌服务接口
 */
public interface BrandService {
    /**
     * 查询所有
     *
     * @return
     */
    List<TbBrand> findAll();

    /**
     * 分页查询
     * @param pageNum 起始页
     * @param pageSize 每页显示的条数
     * @return
     */
    PageResult findPage(int pageNum, int pageSize);

    /**
     * 添加品牌
     *
     * @param brand
     */
    void add(TbBrand brand);

    /**
     * 根据id查询品牌信息
     *
     * @param id
     * @return
     */
    TbBrand findOne(Long id);

    /**
     * 修改品牌信息
     *
     * @param brand
     */
    void update(TbBrand brand);

    /**
     * 删除品牌信息
     *
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 条件查询
     *
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findPage(TbBrand brand, int pageNum, int pageSize);

    /**
     * 查询品牌下拉框数据
     * @return
     */
    public List<Map> selectOption();
}
