package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> page(DishPageQueryDTO dto) {
        log.info("分页查询：{}", dto);
        PageResult result = dishService.pageQuery(dto);
        return Result.success(result);
    }

    /**
     * 查询详情
     */
    @GetMapping("/{id}")
    @ApiOperation("查询菜品详情")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("查询菜品：id={}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 编辑菜品
     */
    @PutMapping
    @ApiOperation("编辑菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("编辑菜品：{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }

    /**
     * 启用/禁用
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用菜品")
    public Result startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("启用/禁用菜品：status={}, id={}", status, id);
        dishService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

}
