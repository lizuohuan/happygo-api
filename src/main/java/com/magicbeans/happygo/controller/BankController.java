package com.magicbeans.happygo.controller;


import com.magicbeans.base.Pages;
import com.magicbeans.base.db.Filter;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.happygo.entity.Bank;
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
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import com.magicbeans.happygo.service.IBankService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;



@RestController
@RequestMapping("/bank")
@Api(description = "银行卡接口")
public class BankController extends BaseController {


    @Resource
    private  IBankService bankService;
    @Resource
    private RedisService redisService;


    @RequestMapping(value = "/getBank",method = RequestMethod.POST)
    @ApiOperation(value = "获取银行卡列表")
    public ResponseData getBankList(Integer pageNO, Integer pageSize) {
        if(CommonUtil.isEmpty(pageNO,pageSize)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("isValid",StatusConstant.YES));
            filters.add(Filter.eq("userId",currentUser.getId()));
            Pages pages = new Pages(pageNO,pageSize);
            Pages<Bank> page = bankService.findPage(pages, filters, null);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                    page.getRecords());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"获取失败");
        }
    }



    @RequestMapping(value = "unbind",method = RequestMethod.POST)
    @ApiOperation(value = "解绑银行")
    @ApiImplicitParam(name = "id",value = "银行ID",required = true)
    public ResponseData unbind(String id) {
        if(CommonUtil.isEmpty(id)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            LoginHelper.getCurrentUser(redisService);
            Bank bank = new Bank();
            bank.setId(id);
            bank.setIsValid(StatusConstant.NO);
            bankService.update(bank);
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"解绑失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"解绑成功");
    }



    @PostMapping(value = "/add")
    @ApiOperation(value = "新增银行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accountName",value = "开户人姓名",required = true),
            @ApiImplicitParam(name = "bankNumber",value = "银行账号",required = true),
            @ApiImplicitParam(name = "bankName",value = "开户行",required = true)
    })
    public ResponseData addBank(String accountName,String bankNumber,String bankName){

        if(CommonUtil.isEmpty(accountName,bankName,bankNumber)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            Bank bank = new Bank();
            bank.setUserId(currentUser.getId());
            bank.setAccountName(accountName);
            bank.setBankName(bankName);
            bank.setBankNumber(bankNumber);
            bankService.save(bank);
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"新增失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"新增成功");
    }

}

