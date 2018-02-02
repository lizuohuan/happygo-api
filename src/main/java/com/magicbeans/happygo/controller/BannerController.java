package com.magicbeans.happygo.controller;

import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.db.Filter;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.service.IBannerService;
import com.magicbeans.happygo.util.StatusConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric Xie on 2018/2/2 0002.
 */

@RequestMapping(value = "/banner")
@RestController
@Api(description = "banner接口")
public class BannerController extends BaseController {

    @Resource
    private IBannerService bannerService;


    @RequestMapping(value = "/getBanner",method = RequestMethod.POST)
    @ApiOperation(value = "获取Banner")
    @ApiImplicitParam(name = "type",value = "Banner配置 参数值：0：首页商品banner  1：外链banner 2:分类Banner")
    public ResponseData getBanner(Integer type){
        List<Filter> filters = new ArrayList<>();
        if(null != type){
            filters.add(Filter.eq("type",type));
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                bannerService.findList(filters,null));
    }

}
