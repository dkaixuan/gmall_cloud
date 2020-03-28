package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kaixuan
 * @version 1.0
 * @date 27/3/2020 下午2:14
 */
@Data
public class AttrGroupVo extends AttrEntity {

    @ApiModelProperty(name = "attrGroupId",value = "attrGroup_relation中间表")
    private Long attrGroupId;
}
