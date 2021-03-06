package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.atguigu.gmall.pms.vo.GroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrDao attrDao;

    @Autowired
    private ProductAttrValueDao attrValueDao;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }



    @Override
    public PageVo getAttrGroupByCategoryId(QueryCondition queryCondition,Long catId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (catId != null) {
            wrapper.eq("catelog_id", catId);
        }
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(queryCondition),wrapper);
        return new PageVo(page);
    }

    /**
     * mybatis写xml文件 sql语句关联三张表
     * @param gid
     * @return
     */
    @Override
    public GroupVo getGroupVo(Long gid) {
        GroupVo groupVo= attrGroupDao.getGroupVo(gid);
        return groupVo;
    }


    /**
     * java 8加mybatisPlus 关联三张表
     * @param gid
     * @return
     */
    @Override
    public GroupVo getGroupVoByMethod2(Long gid) {
        GroupVo groupVo = new GroupVo();
        AttrGroupEntity attrGroupEntity = this.getById(gid);
        BeanUtils.copyProperties(attrGroupEntity,groupVo);

        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_group_id", attrGroupEntity.getAttrGroupId());
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(wrapper);
        if (!CollectionUtils.isEmpty(relationEntities)) {
            groupVo.setRelations(relationEntities);
        }
//此操作等同于下面java8 操作
//        ArrayList<AttrEntity> attrEntities = new ArrayList<>();
//        for (AttrAttrgroupRelationEntity attrAttrgroupRelationEntity : relationEntities) {
//            Long attrId = attrAttrgroupRelationEntity.getAttrId();
//            AttrEntity attrEntity = attrDao.selectById(attrId);
//            attrEntities.add(attrEntity);
//        }
//

        //Java 8 Stream流
        List<Long> attrIds = relationEntities.stream()
                .map(relationEntity -> relationEntity.getAttrId()).collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);
        groupVo.setAttrEntities(attrEntities);


        return groupVo;
    }

    @Override
    public List<GroupVo> listGroupVoByCatalogId(Long catId) {

//        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
//
//      List<GroupVo> = attrGroupEntities.stream().map(attrGroupEntity ->
//            this.getGroupVoByMethod2(attrGroupEntity.getAttrGroupId())).collect(Collectors.toList());

        return  attrGroupDao.listGroupVoByCatalogId(catId);
    }

    @Override
    public List<ItemGroupVo> queryItemGroupVoByCidAndSpuId(Long cid, Long spuId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid));
        return attrGroupEntities.stream().map(group -> {
            ItemGroupVo itemGroupVo = new ItemGroupVo();
            itemGroupVo.setName(group.getAttrGroupName());
            List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", group.getAttrGroupId()));
            List<Long> attr_ids = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId)
                    .collect(Collectors.toList());
            List<ProductAttrValueEntity> productAttrValueEntities = attrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId).in("attr_id", attr_ids));
            itemGroupVo.setBaseAttrs(productAttrValueEntities);
            return itemGroupVo;
        }).collect(Collectors.toList());

    }

}