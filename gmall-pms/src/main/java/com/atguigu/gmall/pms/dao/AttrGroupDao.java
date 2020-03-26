package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.vo.GroupVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author kaixuan
 * @email 670973127@qq.com
 * @date 2020-03-24 15:25:12
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

   GroupVo getGroupVo(@Param("gid") Long gid);

}
