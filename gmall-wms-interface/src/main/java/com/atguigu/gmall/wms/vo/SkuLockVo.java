package com.atguigu.gmall.wms.vo;

import lombok.Data;

/**
 * @author kaixuan
 * @version 1.0
 * @date 7/4/2020 下午4:22
 */
@Data
public class SkuLockVo {
    private Long skuId;
    private Integer count;
    private Long wareSkuId;
    //商品锁定状态
    private Boolean lock;
    private String orderToken;
}
