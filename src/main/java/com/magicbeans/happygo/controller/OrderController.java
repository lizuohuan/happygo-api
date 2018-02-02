package com.magicbeans.happygo.controller;


import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.db.Filter;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.Order;
import com.magicbeans.happygo.entity.OrderProduct;
import com.magicbeans.happygo.entity.ShopCar;
import com.magicbeans.happygo.entity.User;
import com.magicbeans.happygo.exception.InterfaceCommonException;
import com.magicbeans.happygo.redis.RedisService;
import com.magicbeans.happygo.service.IOrderProductService;
import com.magicbeans.happygo.service.IShopCarService;
import com.magicbeans.happygo.util.CommonUtil;
import com.magicbeans.happygo.util.LoginHelper;
import com.magicbeans.happygo.util.StatusConstant;
import com.magicbeans.happygo.vo.ShopCarVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import com.magicbeans.happygo.service.IOrderService;

import javax.annotation.Resource;
import java.util.*;


@RestController
@RequestMapping("/order")
@Api(description = "订单")
public class OrderController extends BaseController {

    @Resource
    private  IOrderService orderService;
    @Resource
    private RedisService redisService;
    @Resource
    private IShopCarService shopCarService;
    @Resource
    private IOrderProductService orderProductService;





    @RequestMapping(value = "/addOrder",method = RequestMethod.POST)
    @ApiOperation(value = "新增订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopCarIds",value = "订单ID集合",required = true),
            @ApiImplicitParam(name = "addressId",value = "地址ID",required = true)
    })
    public ResponseData addOrder(String shopCarIds,String addressId){
        if(CommonUtil.isEmpty(shopCarIds,addressId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            String[] split = shopCarIds.split(",");
            List<String> shopCarIdList = new ArrayList<>();
            shopCarIdList.addAll(Arrays.asList(split));
            if(shopCarIdList.size() == 0){
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数错误");
            }
            Order order = new Order();
            order.setAddressId(addressId);
            order.setStatus(StatusConstant.ORDER_WAITING_PAY);
            order.setOrderNumber(CommonUtil.buildOrderNumber());
            order.setUserId(currentUser.getId());
            orderService.addOrder(order,shopCarIdList);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"操作成功",order.getId());
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"操作失败");
        }
    }


    @RequestMapping(value = "/countOrderPrice",method = RequestMethod.POST)
    @ApiOperation(value = "统计订单的价格，在支付前必须调用")
    @ApiImplicitParam(name = "orderId",value = "订单ID",required = true)
    public ResponseData countOrderPrice(String orderId){
        if(CommonUtil.isEmpty(orderId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            LoginHelper.getCurrentUser(redisService);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"操作成功",
                    orderService.countOrderPrice(orderId));
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"操作失败");
        }
    }

    @RequestMapping(value = "/countOrderNumber",method = RequestMethod.POST)
    @ApiOperation(value = "个人中心统计订单的各个状态数量")
    public ResponseData countOrderNumber(){
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("userId",currentUser.getId()));
            filters.add(Filter.eq("userIsValid",StatusConstant.YES));
            List<Order> list = orderService.findList(filters, null);
            int waitPay = 0;
            int paid = 0;
            int waitSend = 0;
            int sent = 0;
            int finished = 0;
            if(null != list && list.size() > 0){
                for (Order order : list) {
                    if(StatusConstant.ORDER_WAITING_PAY.equals(order.getStatus())){
                        waitPay++;
                    }
                    else if(StatusConstant.ORDER_PAID.equals(order.getStatus())){
                        paid++;
                    }
                    else if(StatusConstant.ORDER_WAITING_SEND.equals(order.getStatus())){
                        waitSend++;
                    }
                    else if(StatusConstant.ORDER_SENT.equals(order.getStatus())){
                        sent++;
                    }
                    else if(StatusConstant.ORDER_FINISHED.equals(order.getStatus())){
                        finished++;
                    }
                }
            }
            Map<String,Integer> map = new HashMap<>();
            map.put("waitPay",waitPay);
            map.put("paid",paid);
            map.put("waitSend",waitSend);
            map.put("sent",sent);
            map.put("finished",finished);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"操作成功",map);
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"操作失败");
        }
    }







}

