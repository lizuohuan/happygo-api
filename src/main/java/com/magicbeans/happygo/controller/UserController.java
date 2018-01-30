package com.magicbeans.happygo.controller;


import com.magicbeans.base.Pages;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.Admin;
import com.magicbeans.happygo.entity.User;
import com.magicbeans.happygo.redis.RedisService;
import com.magicbeans.happygo.service.IAdminService;
import com.magicbeans.happygo.service.IUserService;
import com.magicbeans.happygo.sms.SMSCode;
import com.magicbeans.happygo.util.CommonUtil;
import com.magicbeans.happygo.util.StatusConstant;
import com.magicbeans.happygo.util.TextMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.MessageFormat;
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
@Api(value = "user", description = "用户管理接口")
public class UserController extends BaseController {

    @Resource
    private IUserService userService;
    @Resource
    private RedisService redisService;


    @RequestMapping(value = "/sendCode/{phone}")
    @ApiOperation(value = "注册发送验证码",notes = "验证码的正确性由服务端验证，移动端暂不用验证")
    public ResponseData sendMsg(@PathVariable(value = "phone") String phone){

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



}
