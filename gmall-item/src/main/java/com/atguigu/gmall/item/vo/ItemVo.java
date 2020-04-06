package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.sms.vo.SaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 2/4/2020 下午4:04
 */
@Data
public class ItemVo {

    private Long skuId;
    private CategoryEntity categoryEntity;
    private BrandEntity brandEntity;
    private Long spuId;
    private String spuName;
    private String skuTitle;
    private String subTitle;
    private BigDecimal price;
    private BigDecimal weight;
    //sku图片列表
    private List<SkuImagesEntity> pics;
    //营销信息
    private List<SaleVo> sales;
    //是否有货
    private Boolean store;
    //销售属性
    private List<SkuSaleAttrValueEntity> saleAttrs;
    private List<String> images;
    //规格参数组
    private List<ItemGroupVo> groups;


}
