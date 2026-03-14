package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO 菜品信息（含口味）
     */
    void save(DishDTO dishDTO);

    /**
     * 分页查询菜品
     * @param dto 查询参数
     * @return 分页结果
     */
    PageResult pageQuery(DishPageQueryDTO dto);

    /**
     * 根据id查询菜品
     * @param id 菜品id
     * @return 菜品信息（含口味）
     */
    DishVO getById(Long id);

    /**
     * 编辑菜品
     * @param dishDTO 菜品信息（含口味）
     */
    void update(DishDTO dishDTO);

    /**
     * 启用/禁用菜品
     * @param status 状态
     * @param id 菜品id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 批量删除菜品
     * @param ids 菜品id列表
     */
    void deleteBatch(List<Long> ids);

}
