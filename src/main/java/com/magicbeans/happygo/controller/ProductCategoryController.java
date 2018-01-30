package com.magicbeans.happygo.controller;

import com.magicbeans.base.Pages;
import com.magicbeans.base.ajax.ResponseData;
import com.magicbeans.base.db.Filter;
import com.magicbeans.base.db.Order;
import com.magicbeans.happygo.Message;
import com.magicbeans.happygo.controller.base.BaseController;
import com.magicbeans.happygo.entity.ProductCategory;
import com.magicbeans.happygo.service.IProductCategoryService;
import com.magicbeans.happygo.util.StatusConstant;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类
 * @author lzh
 * @create 2018/1/30 16:37
 */
@RestController
@RequestMapping("/productCategory/")
public class ProductCategoryController extends BaseController {

    @Resource
    private IProductCategoryService productCategoryService;

    /**
     * 封装商品分类
     * @return
     */
    @RequestMapping("list")
    public ResponseData list() {
        //获取全部商品分类
        List<ProductCategory> productCategoryList = productCategoryService.findAll();
        //进行封装
        List<ProductCategory> parentList = new ArrayList<>();
        for (int i = 0; i < productCategoryList.size(); i++) {
            if (null == productCategoryList.get(i).getParentId()) {
                parentList.add(productCategoryList.get(i));
                productCategoryList.remove(i);
                i --;
            }
        }
        for (ProductCategory category : parentList) {
            for (int i = 0; i < productCategoryList.size(); i++) {
                if (category.getId().equals(productCategoryList.get(i).getParentId())) {
                    parentList.add(productCategoryList.get(i));
                    productCategoryList.remove(i);
                    i -- ;
                }
            }
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",parentList);
    }

}
