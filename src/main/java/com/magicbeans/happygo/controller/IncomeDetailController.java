package com.magicbeans.happygo.controller;

import com.magicbeans.base.Pages;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.db.Filter;
import com.magicbeans.base.db.Order;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.IncomeDetail;
import com.magicbeans.happygo.entity.User;
import com.magicbeans.happygo.exception.InterfaceCommonException;
import com.magicbeans.happygo.redis.RedisService;
import com.magicbeans.happygo.service.IIncomeDetailService;
import com.magicbeans.happygo.util.CommonUtil;
import com.magicbeans.happygo.util.LoginHelper;
import com.magicbeans.happygo.util.StatusConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/incomeDetail")
@Api(description = "欢喜券收益")
public class IncomeDetailController extends BaseController {

    @Resource
    private IIncomeDetailService incomeDetailService;
    @Resource
    private RedisService redisService;



    @RequestMapping(value = "/getDetail",method = RequestMethod.POST)
    @ApiOperation(value = "欢喜券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNO",value = "分页参数",required = true),
            @ApiImplicitParam(name = "pageSize",value = "分页参数",required = true)
    })
    public ResponseData getDetail(Integer pageNO,Integer pageSize){

        if(CommonUtil.isEmpty(pageNO,pageSize)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            ArrayList<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("toUserId",currentUser.getId()));
            List<Order> orderList = new ArrayList<>();
            orderList.add(Order.desc("create_time"));
            Pages<IncomeDetail> page = incomeDetailService.findPage(new Pages(pageNO, pageSize), filters
                    , orderList);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",page.getRecords());
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"获取失败");
        }
    }

}
