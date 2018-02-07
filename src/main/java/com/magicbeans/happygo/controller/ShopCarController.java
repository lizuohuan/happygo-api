package com.magicbeans.happygo.controller;


import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.ShopCar;
import com.magicbeans.happygo.entity.User;
import com.magicbeans.happygo.exception.InterfaceCommonException;
import com.magicbeans.happygo.redis.RedisService;
import com.magicbeans.happygo.util.CommonUtil;
import com.magicbeans.happygo.util.LoginHelper;
import com.magicbeans.happygo.util.StatusConstant;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.RequestMapping;
import com.magicbeans.base.ajax.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.magicbeans.happygo.service.IShopCarService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 购物车中间表 前端控制器
 * </p>
 *
 * @author null123
 * @since 2018-02-02
 */
@RestController
@RequestMapping("/shopCar")
@Api(description = "购物车接口列表")
public class ShopCarController extends BaseController {


    @Resource
    private IShopCarService shopCarService;
    @Resource
    private RedisService redisService;


    @ApiOperation(value = "购物车产品操作")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品ID", required = true),
            @ApiImplicitParam(name = "isAdd", value = "0:减  1:加", required = true),
            @ApiImplicitParam(name = "number", value = "产品ID,一般情况下固定值：1，如果该参数值 大于1，则认定该产品的总数量为该值" +
                    " 而不是增加该值", required = true)
    })
    public ResponseData add(ShopCar shopcar, Integer isAdd) {
        if (CommonUtil.isEmpty(shopcar.getNumber(), shopcar.getProductId(), isAdd)) {
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL, "参数不能为空");
        }
        if (shopcar.getNumber() <= 0) {
            return buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION, "参数错误");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            ShopCar car = shopCarService.getShopCarByUser(currentUser.getId(), shopcar.getProductId());
            if (null == car) {
                shopcar.setUserId(currentUser.getId());
                shopCarService.save(shopcar);
            } else {
                int num = 0;
                if (shopcar.getNumber() == 1) {
                    num = isAdd == 0 ? -1 : 1;
                }
                car.setNumber(shopcar.getNumber() > 1 ? shopcar.getNumber() : (car.getNumber() + num));
                shopCarService.update(car);
            }
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return buildFailureJson(StatusConstant.Fail_CODE, "操作失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE, "操作成功");
    }


    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ApiOperation(value = "购物车删除商品")
    @ApiImplicitParam(name = "ids",value = "购物车ID集合，多个以逗号隔开")
    public ResponseData del(String ids) {
        if (CommonUtil.isEmpty(ids)) {
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL, "参数不能为空");
        }
        try {
            LoginHelper.getCurrentUser(redisService);
            List<String> idList = new ArrayList<>();
            String[] split = ids.split(",");
            for (int i = 0; i < split.length; i++) {
                idList.add(split[i]);
            }
            shopCarService.del(idList);
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return buildFailureJson(StatusConstant.Fail_CODE, "操作失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE, "操作成功");
    }



    @RequestMapping(value = "/getShopCar", method = RequestMethod.POST)
    @ApiOperation(value = "获取购物车")
    @ApiImplicitParam(name = "shopCardIds",value = "购物车ID集合，以逗号隔开")
    public ResponseData getShopCar(String shopCardIds) {
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE, "操作成功",
                    shopCarService.getShopCar(currentUser.getId(),shopCardIds));
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return buildFailureJson(StatusConstant.Fail_CODE, "操作失败");
        }

    }





}

