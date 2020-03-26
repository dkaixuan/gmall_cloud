package com.atguigu.gmall.pms.controller;

import com.atguigu.core.bean.OssPolicyResult;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.oss.OssComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kaixuan
 * @version 1.0
 * @date 25/3/2020 下午2:28
 */
@RestController
@RequestMapping("pms/oss")
@Api(tags = "返回阿里云oss签名")
public class OssController {

    @Autowired
    private OssComponent ossComponent;

    @ApiOperation(value = "oss上传签名生成")
    @GetMapping(value = "/policy")
    @ResponseBody
    public Resp policy() {
        OssPolicyResult policy = ossComponent.policy();
         return Resp.ok(policy);
    }
}
