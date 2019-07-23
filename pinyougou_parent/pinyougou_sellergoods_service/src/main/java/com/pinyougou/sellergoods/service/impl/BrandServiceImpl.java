package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /**
     * 分页查询
     *
     * @param pageNum  起始页
     * @param pageSize 每页显示的条数
     * @return
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        if (pageNum < 1 || pageSize < 1) {
            pageNum = 1;
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<TbBrand> tbBrands = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(tbBrands.getTotal(), tbBrands.getResult());
    }

    /**
     * 添加品牌
     *
     * @param brand
     */
    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    /**
     * 根据id查询品牌信息
     *
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(Long id) {
        if (id != null && id > 0) {
            return brandMapper.selectByPrimaryKey(id);
        }
        return null;
    }

    /**
     * 修改品牌信息
     *
     * @param brand
     */
    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    /**
     * 根据id删除品牌信息
     *
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                brandMapper.deleteByPrimaryKey(id);
            }
        }
    }

    /**
     * 条件查询
     *
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if (brand != null) {
            if (brand.getName() != null && brand.getName().length() > 0) {
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<TbBrand> brands = (Page<TbBrand>) brandMapper.selectByExample(example);

        return new PageResult(brands.getTotal(), brands.getResult());
    }

    /**
     * 品牌下拉框数据
     *
     * @return
     */
    @Override
    public List<Map> selectOption() {
        List<TbBrand> tbBrands = brandMapper.selectByExample(null);
        List<Map> list = new ArrayList<Map>();
        for (TbBrand tbBrand : tbBrands) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", tbBrand.getId());
            map.put("text", tbBrand.getName());
            list.add(map);
        }

        return list;
    }
}
