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
    private  IShopCarService shopCarService;
    @Resource
    private RedisService redisService;


    @ApiOperation(value = "添加购物车")
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId",value = "产品ID",required = true),
            @ApiImplicitParam(name = "number",value = "产品ID,一般情况下固定值：1，如果该参数值 大于1，则认定该产品的总数量为该值" +
                    " 而不是增加该值",required = true)
    })
    public ResponseData add(ShopCar shopcar){
        if(CommonUtil.isEmpty(shopcar.getNumber(),shopcar.getProductId())){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        if(shopcar.getNumber() <= 0){
            return buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION,"参数错误");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            ShopCar car = shopCarService.getShopCarByUser(currentUser.getId(), shopcar.getProductId());
            if(null == car){
                shopCarService.save(shopcar);
            }
            else{
                car.setNumber(shopcar.getNumber() > 1 ? shopcar.getNumber() : (car.getNumber() + shopcar.getNumber()));
                shopCarService.update(car);
            }
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        }catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"操作失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"操作成功");
    }


    @RequestMapping(value = "/del",method = RequestMethod.POST)
    @ApiOperation(value = "减除")
    public ResponseData del(String id){
            if(CommonUtil.isEmpty(id)){
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
            }
            try {
                LoginHelper.getCurrentUser(redisService);
                ShopCar shop = shopCarService.find(id);
                if(null != shop ){
                    if(shop.getNumber() == 1){
                        shopCarService.delete(id);
                    }
                    else{
                        shop.setNumber(shop.getNumber() - 1);
                        shopCarService.update(shop);
                    }
                }
            } catch (InterfaceCommonException e) {
                return buildFailureJson(e.getErrorCode(),e.getMessage());
            }catch (Exception e) {
                logger.error(e.getMessage(),e);
                return buildFailureJson(StatusConstant.Fail_CODE,"操作失败");
            }
            return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"操作成功");
    }




}

