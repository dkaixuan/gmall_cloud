package com.atguigu.gmall.pms.controller;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;




/**
 * sku信息
 *
 * @author kaixuan
 * @email 670973127@qq.com
 * @date 2020-03-24 15:25:12
 */
@Api(tags = "sku信息 管理")
@RestController
@RequestMapping("pms/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private AmqpTemplate amqpTemplate;


    private static final String EXCHANGENAME="GMALL_PMS_EXCHANGE";
    private static final String ROUTINGKEY="item.";


    @GetMapping("{spuId}")
    public Resp<List<SkuInfoEntity>> listSkuInfoBySpuId(@PathVariable Long spuId) {
        List<SkuInfoEntity> skuInfoList = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return Resp.ok(skuInfoList);
    }


    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:skuinfo:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = skuInfoService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{skuId}")
    @PreAuthorize("hasAuthority('pms:skuinfo:info')")
    public Resp<SkuInfoEntity> info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return Resp.ok(skuInfo);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:skuinfo:save')")
    public Resp<Object> save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:skuinfo:update')")
    public Resp<Object> update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        amqpTemplate.convertAndSend(EXCHANGENAME, ROUTINGKEY+"update", skuInfo.getSkuId());

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:skuinfo:delete')")
    public Resp<Object> delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return Resp.ok(null);
    }

}
