package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 30/3/2020 下午9:14
 */
@Document(indexName = "goods",type ="info",shards = 3,replicas = 2)
@Data
public class Goods {


    @Id
    private Long skuId;
    @Field(type = FieldType.Keyword,index = false)
    private String pic;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;
    @Field(type=FieldType.Double)
    private Double price;
    @Field(type=FieldType.Long)
    private Long sale;
    //是否有货
    @Field(type=FieldType.Long)
    private Long store;
    @Field(type=FieldType.Date)
    private Date createTime;
    @Field(type=FieldType.Long)
    private Long brandId;
    @Field(type=FieldType.Keyword)
    private String brandName;
    @Field(type=FieldType.Long)
    private Long categoryId;
    @Field(type=FieldType.Keyword)
    private String categoryName;
    //Nested嵌套
    @Field(type=FieldType.Nested)
    private List<SearchAttr> attrs;



}
