package com.atguigu.gmall.pms.service;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.entity.SpuCommentEntity;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 商品评价
 *
 * @author kaixuan
 * @email 670973127@qq.com
 * @date 2020-03-24 15:25:12
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageVo queryPage(QueryCondition params);
}

