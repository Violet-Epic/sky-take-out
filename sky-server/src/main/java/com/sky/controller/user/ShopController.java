package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端 - 店铺营业状态接口
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "用户端-店铺相关接口")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    private final RedisTemplate redisTemplate;

    /**
     * 获取店铺营业状态
     * @return 1=营业中, 0=打烊
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("用户查询营业状态: {}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
