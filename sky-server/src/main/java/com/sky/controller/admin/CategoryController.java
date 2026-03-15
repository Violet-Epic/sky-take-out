package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 新增分类
     */
    @PostMapping
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询分类
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO dto) {
        log.info("分页查询分类：{}", dto);
        PageResult pageResult = categoryService.pageQuery(dto);
        return Result.success(pageResult);
    }

}
