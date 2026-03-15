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

    /**
     * 更新分类
     */
    void update(Category category);

    /**
     * 根据id查询分类
     */
    Category getById(Long id);

    /**
     * 根据id删除分类
     */
    void deleteById(Long id);
}
