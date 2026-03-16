package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    private static final String DISH_KEY = "dish:";

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增菜品
     */
    @Transactional
    @Override
    public void save(DishDTO dishDTO) {
        // 1. 插入菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.ENABLE);
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setUpdateUser(BaseContext.getCurrentId());

        dishMapper.insert(dish);

        // 2. 插入口味（需要菜品id）
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            Long dishId = dish.getId();
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<Dish> dishes = dishMapper.query(dto.getName(),
                dto.getCategoryId() != null ? dto.getCategoryId().longValue() : null,
                dto.getStatus());
        PageInfo<Dish> pageInfo = new PageInfo<>(dishes);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 根据id查询菜品
     */
    @Override
    public DishVO getById(Long id) {
        String key = DISH_KEY + id;

        // 1. 先查缓存
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            log.info("缓存命中: {}", key);
            return JSON.parseObject(json, DishVO.class);
        }

        // 2. 缓存没有，查数据库
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        // 组装 VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);

        // 3. 存入缓存（1小时过期）
        redisTemplate.opsForValue().set(key, JSON.toJSONString(dishVO), 1, TimeUnit.HOURS);
        log.info("存入缓存: {}", key);

        return dishVO;
    }

    /**
     * 编辑菜品
     */
    @Transactional
    @Override
    public void update(DishDTO dishDTO) {
        // 1. 更新菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setUpdateTime(LocalDateTime.now());
        dish.setUpdateUser(BaseContext.getCurrentId());
        dishMapper.update(dish);

        // 2. 删除旧口味
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        // 3. 插入新口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }

        // 4. 清除缓存
        redisTemplate.delete(DISH_KEY + dishDTO.getId());
        log.info("清除缓存: {}", DISH_KEY + dishDTO.getId());
    }

    /**
     * 启用/禁用菜品
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        dishMapper.update(dish);

        // 清除缓存
        redisTemplate.delete(DISH_KEY + id);
        log.info("清除缓存: {}", DISH_KEY + id);
    }

    /**
     * 批量删除
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            // 删除口味
            dishFlavorMapper.deleteByDishId(id);
            // 删除菜品
            dishMapper.deleteById(id);
            // 清除缓存
            redisTemplate.delete(DISH_KEY + id);
        }
        log.info("清除缓存: {}", ids);
    }

}
