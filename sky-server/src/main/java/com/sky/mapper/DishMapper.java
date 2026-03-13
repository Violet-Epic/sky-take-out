package com.sky.mapper;

import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 新增菜品
     * @param dish 菜品对象
     */
    void insert(Dish dish);

    /**
     * 分页查询菜品
     * @param name 菜品名称（模糊查询）
     * @param categoryId 分类id
     * @param status 状态
     * @return 菜品列表
     */
    List<Dish> query(String name, Long categoryId, Integer status);

    /**
     * 根据id查询菜品
     * @param id 菜品id
     * @return 菜品对象
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据id更新菜品
     * @param dish 菜品对象
     */
    void update(Dish dish);

    /**
     * 根据分类id查询菜品数量
     * @param categoryId 分类id
     * @return 菜品数量
     */
    @Select("select count(*) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 根据id删除菜品
     * @param id 菜品id
     */
    @Select("delete from dish where id = #{id}")
    void deleteById(Long id);

}
