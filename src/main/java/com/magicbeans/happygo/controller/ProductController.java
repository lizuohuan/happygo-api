package com.magicbeans.happygo.controller;

import com.magicbeans.base.Pages;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.db.Filter;
import com.magicbeans.base.db.Order;
import com.magicbeans.happygo.Message;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.Product;
import com.magicbeans.happygo.entity.ProductCategory;
import com.magicbeans.happygo.service.IProductCategoryService;
import com.magicbeans.happygo.service.IProductService;
import com.magicbeans.happygo.util.CommonUtil;
import com.magicbeans.happygo.util.StatusConstant;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 商品
 * @author lzh
 * @create 2018/1/30 15:50
 */
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Resource
    private IProductService productService;

    @Resource
    private IProductCategoryService productCategoryService;




    @RequestMapping(value = "/index",method = RequestMethod.POST)
    @ApiOperation(value = "商品列表首页")
    public ResponseData index(){
        List<Filter> filters = new ArrayList<>();
        filters.add(Filter.or(
                Filter.eq("isPromotion",1),
                Filter.eq("isIntegral",1),
                Filter.eq("isHot",1)));
        List<Product> productList = productService.findList(filters,Order.desc("id"));

        Map<String,List<Product>> map = new HashMap<>();
        //积分商品
        List<Product> integralList = new ArrayList<>();
        //推荐商品
        List<Product> hotList = new ArrayList<>();
        //促销商品
        List<Product> promotionList = new ArrayList<>();
        for (Product product : productList) {
            if (null != product.getIsIntegral() && product.getIsIntegral() == 1) {
                integralList.add(product);
                continue;
            }
            if (null != product.getIsHot() && product.getIsHot() == 1) {
                hotList.add(product);
                continue;
            }
            if (null != product.getIsPromotion() && product.getIsPromotion() == 1) {
                promotionList.add(product);
                continue;
            }
        }
        map.put("integralList",integralList);
        map.put("hotList",hotList);
        map.put("promotionList",promotionList);
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",map);
    }



    @RequestMapping(value = "/searchProduct",method = RequestMethod.POST)
    @ApiOperation(value = "搜索商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName",value = "商品名称"),
            @ApiImplicitParam(name = "pageNO",value = "分页参数",required = true),
            @ApiImplicitParam(name = "pageSize",value = "分页参数",required = true)
    })
    public ResponseData searchProduct(String productName,Integer pageNO,Integer pageSize){
       if(CommonUtil.isEmpty(pageNO,pageSize)){
           return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"参数不能为空");
       }
        productName = CommonUtil.isEmpty(productName) ? null : productName;
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                productService.searchProduct(productName, pageNO, pageSize));
    }


    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @GetMapping("info")
    public ResponseData info(String id) {

        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",productService.find(id));
    }




    /**
     * 获取商品分类名
     * @param productCategoryId
     * @param productCategoryList
     * @return
     */
    private String getProductCategoryName(String productCategoryId,List<ProductCategory> productCategoryList) {
        String productCategoryName = "";
        for (ProductCategory category : productCategoryList) {
            if (category.getId().equals(productCategoryId)) {
                productCategoryName = category.getName();
                if (null != category.getParentId() ) {
                    productCategoryName = getProductCategoryName(category.getParentId(),productCategoryList) + "," + productCategoryName;
                }
                break;
            }
        }
        return productCategoryName;
    }


}
