package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 新增套餐
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐
     */
    PageResult pageQuery(SetmealPageQueryDTO dto);

    /**
     * 根据id查询套餐
     */
    SetmealVO getById(Long id);

    /**
     * 编辑套餐
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 启用/禁用套餐
     */
    void startOrStop(Integer status, Long id);

    /**
     * 批量删除套餐
     */
    void deleteBatch(List<Long> ids);

}
