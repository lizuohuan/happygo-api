package com.magicbeans.happygo.controller;

import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.service.ISystemConfigService;
import com.magicbeans.happygo.util.StatusConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Eric Xie on 2018/2/2 0002.
 */
@RestController
@RequestMapping("/config")
@Api(description = "配置接口")
public class ConfigController extends BaseController {

    @Resource
    private ISystemConfigService systemConfigService;


    @RequestMapping(value = "/getConfig",method = RequestMethod.POST)
    @ApiOperation(value = "获取全局配置")
    public ResponseData getConfig(){
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                systemConfigService.findAll().get(0));
    }

}
