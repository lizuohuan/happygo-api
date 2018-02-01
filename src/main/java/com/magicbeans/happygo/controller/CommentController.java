package com.magicbeans.happygo.controller;

import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.Comment;
import com.magicbeans.happygo.entity.User;
import com.magicbeans.happygo.exception.InterfaceCommonException;
import com.magicbeans.happygo.redis.RedisService;
import com.magicbeans.happygo.service.ICommentService;
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

@RestController
@RequestMapping("/comment")
@Api(description = "商品评论")
public class CommentController extends BaseController {

    @Resource
    private ICommentService commentService;
    @Resource
    private RedisService redisService;


    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ApiOperation(value = "提交评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId",value = "商品ID",required = true),
            @ApiImplicitParam(name = "score",value = "得分，百分制",required = true),
            @ApiImplicitParam(name = "remark",value = "评论内容"),
            @ApiImplicitParam(name = "img",value = "评论图片，多图使用逗号隔开上传")
    })
    public ResponseData add(Comment comment){
        if(CommonUtil.isEmpty(comment.getProductId(),comment.getScore())){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        try {
            User currentUser = LoginHelper.getCurrentUser(redisService);
            comment.setUserId(currentUser.getId());
            commentService.save(comment);
        } catch (InterfaceCommonException e) {
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"提交失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"提交成功");
    }


    @RequestMapping(value = "/getComment",method = RequestMethod.POST)
    @ApiOperation(value = "获取商品评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId",value = "商品ID",required = true),
            @ApiImplicitParam(name = "pageNO",value = "分页参数 从1开始",required = true),
            @ApiImplicitParam(name = "pageSize",value = "分页参数",required = true)
    })
    public ResponseData getComment(String productId,Integer pageNO,Integer pageSize){

        if(CommonUtil.isEmpty(productId,pageNO,pageSize)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                commentService.getCommentByProduct(productId,pageNO,pageSize));
    }


    @RequestMapping(value = "/countComment",method = RequestMethod.POST)
    @ApiOperation(value = "获取商品的 评论总数 和 好评率")
    @ApiImplicitParam(name = "productId",value = "商品ID",required = true)
    public ResponseData countComment(String productId){
        if(CommonUtil.isEmpty(productId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                commentService.countProductComment(productId));
    }

}
