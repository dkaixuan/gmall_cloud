package com.atguigu.gmall.pms.entity;

import lombok.Data;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 2/4/2020 下午4:40
 */
@Data
public class ItemGroupVo {
    private String name;
    private List<ProductAttrValueEntity> baseAttrs;
}
