package com.sky.mapper;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 新增分类
     */
    void insert(Category category);

    /**
     * 分页查询分类
     */
    List<Category> query(CategoryPageQueryDTO dto);
}
