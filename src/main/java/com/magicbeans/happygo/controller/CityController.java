package com.magicbeans.happygo.controller;

import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.service.ICityService;
import com.magicbeans.happygo.util.StatusConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Eric Xie on 2018/2/2 0002.
 */

@RestController
@RequestMapping(value = "/city")
@Api(description = "城市相关接口列表")
public class CityController extends BaseController {

    @Resource
    private ICityService cityService;



    @RequestMapping(value = "/getCity",method = RequestMethod.POST)
    @ApiOperation(value = "获取城市列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId",value = "父ID，如果查询所有的省级城市不需传"),
            @ApiImplicitParam(name = "levelType",value = "级别 1：省级，2：市级 3：区县",required = true)
    })
    public ResponseData getCity(Integer parentId,Integer levelType) {
        if(null == levelType){
            return buildFailureJson(StatusConstant.Fail_CODE,"参数不能为空");
        }
        if(levelType > 3 || levelType <= 0){
            return buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION,"参数错误");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                cityService.getCityByParentId(parentId,levelType));

    }
}
