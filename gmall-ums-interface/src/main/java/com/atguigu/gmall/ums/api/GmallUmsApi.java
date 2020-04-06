package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author kaixuan
 * @version 1.0
 * @date 3/4/2020 下午8:52
 */
public interface GmallUmsApi {


    @GetMapping("ums/member/query")
     Resp<MemberEntity> queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password);


}
