package com.sky.service.impl;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.service.CategoryService;
import com.sky.context.BaseContext;
import com.sky.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public Category addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        // 补充 DTO 没有的字段
        category.setStatus(1); // 默认启用
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.insert(category);
        return category;
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO dto) {
        // 1. 开启分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        // 2. 查询
        Page<Category> page = (Page<Category>) categoryMapper.query(dto);

        // 3. 封装结果
        return new PageResult(page.getTotal(), page.getResult());
    }
}
