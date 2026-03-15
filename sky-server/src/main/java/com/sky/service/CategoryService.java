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
}
