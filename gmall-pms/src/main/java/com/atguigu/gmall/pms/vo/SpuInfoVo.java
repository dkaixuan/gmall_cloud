package com.atguigu.gmall.pms.vo;


import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 28/3/2020 上午10:47
 */
@Data
public class SpuInfoVo extends SpuInfoEntity {

    private List<String> spuImages;

    private List<BaseAttrVo> baseAttrs;

    private List<SkuInfoVo> skus;



}
