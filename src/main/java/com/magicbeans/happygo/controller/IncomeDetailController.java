package com.magicbeans.happygo.controller;

import com.magicbeans.base.Pages;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.db.Filter;
import com.magicbeans.base.db.Order;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.IncomeDetail;
import com.magicbeans.happygo.entity.User;
import com.magicbeans.happygo.entity.UserScore;
import com.magicbeans.happygo.exception.InterfaceCommonException;
import com.magicbeans.happygo.redis.RedisService;
import com.magicbeans.happygo.service.IIncomeDetailService;
import com.magicbeans.happygo.service.IUserScoreService;
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
@Api(description = "欢喜券|积分相关接口")
public class IncomeDetailController extends BaseController {

    @Resource
    private IIncomeDetailService incomeDetailService;
    @Resource
    private RedisService redisService;
    @Resource
    private IUserScoreService userScoreService;



    @RequestMapping(value = "/getDetail",method = RequestMethod.POST)
    @ApiOperation(value = "欢喜券列表|收支明细|收益明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNO",value = "分页参数",required = true),
            @ApiImplicitParam(name = "pageSize",value = "分页参数",required = true),
            @ApiImplicitParam(name = "isIncome",value = "0:收支记录  1:收益记录",required = true)
    })
    public ResponseData getDetail(Integer pageNO,Integer pageSize,Integer isIncome){

        if(CommonUtil.isEmpty(pageNO,pageSize,isIncome)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        if(0 != isIncome && 1 != isIncome){
            return buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION,"参数错误");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            ArrayList<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("toUserId",currentUser.getId()));
            if(1 == isIncome){
                filters.add(Filter.ne("type",2));
            }
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



    @RequestMapping(value = "/countIncome",method = RequestMethod.POST)
    @ApiOperation(value = "欢喜券收益统计",notes = "欢喜券页 统计 今日收益(todayIncome)、总券(balance)、" +
            " 总收益(totalIncome)、欢喜积分(totalScore)、分销收益(distributionIncome)")
    public ResponseData countIncome(){
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                    incomeDetailService.countIncome(currentUser.getId()));
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"获取失败");
        }
    }





    @RequestMapping(value = "/getUserScore",method = RequestMethod.POST)
    @ApiOperation(value = "积分明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNO",value = "分页参数",required = true),
            @ApiImplicitParam(name = "pageSize",value = "分页参数",required = true)
    })
    public ResponseData getUserScore(Integer pageNO,Integer pageSize){
        if(CommonUtil.isEmpty(pageNO,pageSize)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            ArrayList<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("userId",currentUser.getId()));
            List<Order> orderList = new ArrayList<>();
            orderList.add(Order.desc("create_time"));
            Pages<UserScore> page = userScoreService.findPage(new Pages(pageNO, pageSize), filters
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
