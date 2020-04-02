package com.atguigu.gmall.search.pojo;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author kaixuan
 * @version 1.0
 * @date 31/3/2020 下午12:50
 */
public class SearchAttr {

    @Field(type = FieldType.Long)
    private Long attrId;
    @Field(type =FieldType.Keyword)
    private String attrName;
    @Field(type =FieldType.Keyword)
    private String attrValue;


}
