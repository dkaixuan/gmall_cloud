package com.atguigu.gmall.sms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀活动场次
 * 
 * @author kaixuan
 * @email 670973127@qq.com
 * @date 2020-03-24 22:06:57
 */
@ApiModel
@Data
@TableName("sms_seckill_session")
public class SeckillSessionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	@ApiModelProperty(name = "id",value = "id")
	private Long id;
	/**
	 * 场次名称
	 */
	@ApiModelProperty(name = "name",value = "场次名称")
	private String name;
	/**
	 * 每日开始时间
	 */
	@ApiModelProperty(name = "startTime",value = "每日开始时间")
	private Date startTime;
	/**
	 * 每日结束时间
	 */
	@ApiModelProperty(name = "endTime",value = "每日结束时间")
	private Date endTime;
	/**
	 * 启用状态
	 */
	@ApiModelProperty(name = "status",value = "启用状态")
	private Integer status;
	/**
	 * 创建时间
	 */
	@ApiModelProperty(name = "createTime",value = "创建时间")
	private Date createTime;

}
