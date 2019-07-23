package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/brand")
@RestController
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有的品牌信息
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    /**
     * 普通分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int size) {
        return brandService.findPage(page, size);
    }

    /**
     * 添加品牌
     *
     * @param brand
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 根据id查询品牌信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id) {
        return brandService.findOne(id);
    }

    /**
     * 修改品牌信息
     *
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand) {
        if (brand != null && brand.getId() != null) {
            brandService.update(brand);
            return new Result(true, "修改成功");
        } else {
            return new Result(false, "修改失败");
        }
    }

    /**
     * 删除品牌信息
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            brandService.delete(ids);
            return new Result(true, "删除成功");
        } else {
            return new Result(false, "删除失败");
        }
    }

    /**
     * 条件分页查询
     *
     * @param brand
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/search")
    public PageResult findPage(@RequestBody TbBrand brand, int page, int size) {
        return brandService.findPage(brand, page, size);
    }

    /**
     * 查询品牌下拉框数据
     * @return
     */
    @RequestMapping("/selectOptionList")
    public List<Map> SelectOptionList(){
        return brandService.selectOption();
    }
}
