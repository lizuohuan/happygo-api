package com.magicbeans.happygo.controller;


import com.magicbeans.base.Pages;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.db.Filter;
import com.magicbeans.base.db.Order;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.Address;
import com.magicbeans.happygo.entity.User;
import com.magicbeans.happygo.exception.InterfaceCommonException;
import com.magicbeans.happygo.redis.RedisService;
import com.magicbeans.happygo.util.CommonUtil;
import com.magicbeans.happygo.util.LoginHelper;
import com.magicbeans.happygo.util.StatusConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.magicbeans.happygo.service.IAddressService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/address")
@Api(description = "用户地址管理")
public class AddressController extends BaseController {

    @Resource
    private  IAddressService addressService;
    @Resource
    private RedisService redisService;


    @ApiOperation(value = "获取默认地址")
    @PostMapping(value = "/getDefaultAddress")
    public ResponseData getDefaultAddress() {
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                    addressService.getDefaultAddress(currentUser.getId()));
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"操作失败");
        }
    }


    @ApiOperation(value = "获取地址列表")
    @PostMapping(value = "/getAddressList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNO",value = "分页参数",required = true),
            @ApiImplicitParam(name = "pageSize",value = "分页参数",required = true)
    })
    public ResponseData getAddressList(Integer pageNO,Integer pageSize) {
        if(CommonUtil.isEmpty(pageNO,pageSize)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                    addressService.getAddressList(currentUser.getId(),pageNO,pageSize));
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"操作失败");
        }
    }



    @ApiOperation(value = "删除地址")
    @PostMapping(value = "/delAddress")
    @ApiImplicitParam(name = "id",value = "地址ID",required = true)
    public ResponseData deleteById(String id) {
        if(CommonUtil.isEmpty(id)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        Address address = addressService.find(id);
        if(null == address){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"地址不存在");
        }
        address.setIsValid(StatusConstant.NO);
        addressService.update(address);
        return ResponseData.success();
    }


    @ApiOperation(value = "查询地址")
    @RequestMapping(value = "/getAddress",method = RequestMethod.POST)
    @ApiImplicitParam(name = "id",value = "地址ID",required = true)
    public ResponseData getAddress(String id){
        if(CommonUtil.isEmpty(id)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE ,"获取成功",addressService.find(id));
    }


    @ApiOperation(value = "地址新增/修改接口")
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cityId",value = "城市ID，区县级",required = true),
            @ApiImplicitParam(name = "contacts",value = "联系人",required = true),
            @ApiImplicitParam(name = "phone",value = "联系电话",required = true),
            @ApiImplicitParam(name = "detailAddress",value = "详细地址",required = true),
            @ApiImplicitParam(name = "id",value = "地址ID，如果为空则新增地址，不为空修改地址"),
            @ApiImplicitParam(name = "isDefault",value = "是否为默认地址 0：否 1：是",required = true)
    })
    public ResponseData add(Address address){
        if(CommonUtil.isEmpty(address.getCityId(),address.getContacts(),address.getDetailAddress(),
                address.getPhone(),address.getIsDefault())){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            if(1 == address.getIsDefault()){
                List<Filter> filters = new ArrayList<>();
                filters.add(Filter.eq("userId",currentUser.getId()));
                filters.add(Filter.eq("isDefault",1));
                filters.add(Filter.eq("isValid",1));
                List<Address> addressList = addressService.findList(filters,null);
                if(null != addressList && addressList.size() > 0){
                    for (Address a : addressList) {
                        a.setIsDefault(0);
                        addressService.update(a);
                    }
                }
            }
            if(CommonUtil.isEmpty(address.getId())){
                address.setUserId(currentUser.getId());
                addressService.save(address);
            }
            else{
                // 更新操作
                addressService.update(address);
            }
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"操作失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"操作成功");
    }

}

