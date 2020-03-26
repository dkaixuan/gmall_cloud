package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 26/3/2020 下午6:57
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class GroupVo extends AttrGroupEntity {

    private List<AttrEntity> attrEntities=new ArrayList<>();
    private List<AttrAttrgroupRelationEntity> relations=new ArrayList<>();

}
