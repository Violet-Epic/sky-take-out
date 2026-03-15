package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

public interface CategoryService {

    /**
     * 新增分类
     */
    Category addCategory(CategoryDTO categoryDTO);

    /**
     * 分页查询分类
     */
    PageResult pageQuery(CategoryPageQueryDTO dto);

    /**
     * 启用/禁用分类
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询分类
     */
    Category getById(Long id);

    /**
     * 编辑分类
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 删除分类
     */
    void deleteById(Long id);
}
