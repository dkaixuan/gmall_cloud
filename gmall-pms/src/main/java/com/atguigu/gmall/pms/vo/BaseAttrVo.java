package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 28/3/2020 上午10:52
 */
@Data
public class BaseAttrVo extends ProductAttrValueEntity {

    public void setValueSelected(List<String> valueSelected) {
        if (CollectionUtils.isEmpty(valueSelected)) {
            return;
        }

        //把集合转换成 用 逗号分隔的字符串
        this.setAttrValue(StringUtils.join(valueSelected, ","));
    }



}
