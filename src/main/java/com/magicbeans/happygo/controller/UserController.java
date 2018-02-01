package com.magicbeans.happygo.controller;


import com.magicbeans.base.Pages;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.Admin;
import com.magicbeans.happygo.entity.User;
import com.magicbeans.happygo.exception.InterfaceCommonException;
import com.magicbeans.happygo.redis.RedisService;
import com.magicbeans.happygo.service.IAdminService;
import com.magicbeans.happygo.service.IIncomeDetailService;
import com.magicbeans.happygo.service.IUserService;
import com.magicbeans.happygo.sms.SMSCode;
import com.magicbeans.happygo.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author magic-beans
 * @since 2017-07-28
 */
@RestController
@RequestMapping("/user")
@Api(description = "用户管理接口")
public class UserController extends BaseController {

    @Resource
    private IUserService userService;
    @Resource
    private RedisService redisService;
    @Resource
    private IIncomeDetailService incomeDetailService;


    @RequestMapping(value = "/sendCode",method = RequestMethod.POST)
    @ApiOperation(value = "注册发送验证码",notes = "验证码的正确性由服务端验证，移动端暂不用验证 ")
    @ApiImplicitParam(name = "phone",value = "手机号码" ,required = true)
    public ResponseData sendMsg(String phone){

        if(CommonUtil.isEmpty(phone)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        User user = userService.getUserByPhone(phone);
        if(null != user){
            return buildFailureJson(StatusConstant.OBJECT_EXIST,"手机号已经存在");
        }
        String code = SMSCode.createRandomCode();
        String msg = MessageFormat.format(TextMessage.MSG_CODE, code);
        boolean isSuccess = SMSCode.sendMessage(msg, phone);
        if(!isSuccess){
            return buildFailureJson(StatusConstant.Fail_CODE,"发送失败");
        }
        redisService.set(TextMessage.REDIS_KEY_PREFIX + phone,code,TextMessage.EXPIRE_TIME, TimeUnit.MINUTES);
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"发送成功",code);
    }


    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ApiOperation(value = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone",value = "手机号",required = true),
            @ApiImplicitParam(name = "code",value = "验证码",required = true),
            @ApiImplicitParam(name = "pwd",value = "加过密的文本",required = true),
            @ApiImplicitParam(name = "deviceType",value = "设备类型，0:android  1:ios 其他不传"),
            @ApiImplicitParam(name = "deviceToken",value = "设备请求的推送token")
    })
    public ResponseData register(String phone,String code,String pwd,
                                 String deviceToken,Integer deviceType){
        if(CommonUtil.isEmpty(phone,code,pwd)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        Object o = redisService.get(TextMessage.REDIS_KEY_PREFIX + phone);
        if(null == o || !code.equals(o.toString())){
            return buildFailureJson(StatusConstant.Fail_CODE,"验证码失效");
        }
        User user = userService.getUserByPhone(phone);
        if(null != user){
            return buildFailureJson(StatusConstant.OBJECT_EXIST,"手机号已经存在");
        }
        User r = new User();
        r.setPhone(phone);
        r.setPwd(pwd);
        r.setDeviceToken(deviceToken);
        r.setDeviceType(deviceType);
        r.setRoleId(RoleConstant.REGULAR_MEMBERS);
        userService.save(r);

        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisService.set(token,r,StatusConstant.LOGIN_VALID,TimeUnit.DAYS);
        r.setToken(token);
        userService.update(r);
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"注册成功",r);
    }



    @RequestMapping(value = "/setPayPwd",method = RequestMethod.POST)
    @ApiOperation(value = "设置支付密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "payPwd",value = "加过密的支付密码",required = true)
    })
    public ResponseData setPayPwd(String payPwd){
        if(CommonUtil.isEmpty(payPwd)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            User user = LoginHelper.getCurrentUser(redisService);
            User u = new User();
            u.setId(user.getId());
            u.setPayPwd(payPwd);
            userService.update(u);
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"设置失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"设置成功");
    }



    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ApiOperation(value = "常规登录(非微信、QQ)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone",value = "手机号",required = true),
            @ApiImplicitParam(name = "pwd",value = "密码",required = true),
            @ApiImplicitParam(name = "deviceToken",value = "设备token"),
            @ApiImplicitParam(name = "deviceType",value = "设备类型 0 android  1 ios")
    })
    public ResponseData login(String phone,String pwd,String deviceToken,Integer deviceType){

        if(CommonUtil.isEmpty(phone,pwd)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        User user = userService.getUserByPhone(phone);
        if(null == user || StatusConstant.INVALID.equals(user.getStatus())){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"手机号不存在");
        }
        if(!pwd.equals(user.getPwd())){
            return buildFailureJson(StatusConstant.Fail_CODE,"密码错误");
        }
        user.setDeviceToken(deviceToken);
        user.setDeviceType(deviceType);
        user.setPayPwd(null);
        if(!CommonUtil.isEmpty(user.getToken())){
            redisService.remove(user.getToken());
        }
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisService.set(token,user,StatusConstant.LOGIN_VALID,TimeUnit.DAYS);
        user.setToken(token);
        userService.update(user);
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"登录成功",user);
    }


    @RequestMapping(value = "/getInfo",method = RequestMethod.POST)
    @ApiOperation(value = "获取个人基本信息")
    public ResponseData getInfo(){
        try {
            User user = LoginHelper.getCurrentUser(redisService);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",user);
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"设置失败");
        }
    }


    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ApiOperation(value = "更新用户字段操作")
    public ResponseData setBaseInfo(User user){
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            user.setPayPwd(null);
            user.setId(currentUser.getId());
            userService.update(user);

            User sql = userService.find("id", user.getId());
            redisService.set(currentUser.getToken(),sql,StatusConstant.LOGIN_VALID,TimeUnit.DAYS);
            return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"操作成功");
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"设置失败");
        }
    }


    @RequestMapping(value = "/getDistributionUser",method = RequestMethod.POST)
    @ApiOperation(value = "获取当前分销用户",notes = "返回格式：{'one':[],'two':[],'three':[]}")
    public ResponseData getDistributionUser(){
        try {
            User user = LoginHelper.getCurrentUser(redisService);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                    userService.getDistributionUser(user.getId(),null,null));
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"设置失败");
        }
    }



    @RequestMapping(value = "/getIncomeDetail",method = RequestMethod.POST)
    @ApiOperation(value = "获取收益明细集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromUserId",value = "明细来源用户ID",required = true),
            @ApiImplicitParam(name = "pageNO",value = "分页参数 从 1 开始",required = true),
            @ApiImplicitParam(name = "pageSize",value = "分页参数",required = true)
    })
    public ResponseData getIncomeDetail(String fromUserId,Integer pageNO,Integer pageSize){

        if(CommonUtil.isEmpty(fromUserId,pageNO,pageSize)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            User user = LoginHelper.getCurrentUser(redisService);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                    incomeDetailService.getIncomeDetail(fromUserId,user.getId(),pageNO,pageSize));
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"设置失败");
        }

    }







}
