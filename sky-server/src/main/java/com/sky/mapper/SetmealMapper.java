package com.sky.mapper;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 新增套餐
     */
    void insert(Setmeal setmeal);

    /**
     * 分页查询套餐
     */
    List<Setmeal> query(SetmealPageQueryDTO dto);

    /**
     * 根据id查询套餐
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 更新套餐
     */
    void update(Setmeal setmeal);

    /**
     * 根据id删除套餐
     */
    @Select("delete from setmeal where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据分类id查询套餐数量
     */
    @Select("select count(*) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

}
