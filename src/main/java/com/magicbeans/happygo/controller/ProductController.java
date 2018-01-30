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
import com.magicbeans.happygo.util.StatusConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 商品
 * @author lzh
 * @create 2018/1/30 15:50
 */
@RestController
@RequestMapping("/product/")
public class ProductController extends BaseController {

    @Resource
    private IProductService productService;

    @Resource
    private IProductCategoryService productCategoryService;



    /**
     * 主页分类商品
     * @return
     */
    @RequestMapping(value = "index")
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
        //获取全部商品分类
//        List<ProductCategory> productCategoryList = productCategoryService.findAll();
//        for (Product product : productList) {
//            //设置商品分类名
//            product.setProductCategoryName(getProductCategoryName(product.getProductCategoryId(),productCategoryList));
//        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",map);
    }


    /**
     * 商品列表
     * @param page
     * @return
     */
    @RequestMapping(value = "list")
    public ResponseData list(Pages<Product> page, String name , String number , Integer productCategoryId,
                             Integer isPromotion , Integer isIntegral , Integer isHot ,
                             Long createTimeStart , Long createTimeEnd){
        List<Filter> filters = new ArrayList<>();
        filters.add(Filter.like("name",name));
        filters.add(Filter.like("number",number));
        filters.add(Filter.eq("productCategoryId",productCategoryId));
        filters.add(Filter.eq("isPromotion",isPromotion));
        filters.add(Filter.eq("isIntegral",isIntegral));
        filters.add(Filter.eq("isHot",isHot));
        filters.add(Filter.between("create_time",new Date(createTimeStart),new Date(createTimeEnd)));
        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("id"));
        page = productService.findPage(page,filters,orders);

        //获取全部商品分类
        List<ProductCategory> productCategoryList = productCategoryService.findAll();
        for (Product product : page.getRecords()) {
            //设置商品分类名
            product.setProductCategoryName(getProductCategoryName(product.getProductCategoryId(),productCategoryList));
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",page);
    }


    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @GetMapping("info")
    public ResponseData info(String id) {
//        List<ProductCategory> parentList = getProductCategories();

        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",productService.find(id));
    }




    /**
     * 获取商品分类名
     * @param productCategoryId
     * @param productCategoryList
     * @return
     */
    private String getProductCategoryName(Integer productCategoryId,List<ProductCategory> productCategoryList) {
        String productCategoryName = "";
        for (ProductCategory category : productCategoryList) {
            if (category.getId().equals(productCategoryId)) {
                productCategoryName = category.getName();
                if (null != category.getParentId() ) {
                    productCategoryName = getProductCategoryName(productCategoryId,productCategoryList) + "," + productCategoryName;
                }
                break;
            }
        }
        return productCategoryName;
    }


}
