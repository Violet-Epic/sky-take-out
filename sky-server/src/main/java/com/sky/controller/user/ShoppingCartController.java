package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端 - 购物车接口
 */
@RestController("userShoppingCartController")
@RequestMapping("/user/shoppingCart")
@Api(tags = "用户端-购物车相关接口")
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping
    @ApiOperation("添加购物车")
    public Result<?> add(@RequestBody ShoppingCartDTO dto) {
        shoppingCartService.add(dto);
        return Result.success();
    }

    /**
     * 查看购物车
     */
    @GetMapping
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    /**
     * 删除购物车中一个商品
     */
    @PostMapping("/sub")
    @ApiOperation("删除购物车中一个商品")
    public Result<?> sub(@RequestBody ShoppingCartDTO dto) {
        shoppingCartService.deleteOne(dto);
        return Result.success();
    }

    /**
     * 清空购物车
     */
    @DeleteMapping
    @ApiOperation("清空购物车")
    public Result<?> clean() {
        shoppingCartService.clean();
        return Result.success();
    }
}
