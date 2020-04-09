package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    @GetMapping("ums/memberreceiveaddress/{userId}")
     Resp<List<MemberReceiveAddressEntity>> queryAddressByUserId(@PathVariable Long userId);

    @GetMapping("ums/member/info/{id}")
     Resp<MemberEntity> queryMemberById(@PathVariable("id") Long id);

}
