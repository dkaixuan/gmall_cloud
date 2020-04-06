package com.atguigu.gmall.ums.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        switch (type) {
            case 1:wrapper.eq("username",data);
                break;
            case 2:wrapper.eq("mobile",data);
                break;
            case 3:wrapper.eq("email",data);
                break;
            default:
                return false;
        }
        return this.count(wrapper)==0;
    }



    @Override
    public void register(MemberEntity memberEntity, String code) {
        //从redis 取code验证


        String salt = RandomUtil.randomString(6);
        memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword() + salt));
        memberEntity.setSalt(salt);
        memberEntity.setGrowth(0);
        memberEntity.setIntegration(0);
        memberEntity.setLevelId(0L);
        memberEntity.setStatus(1);
        this.save(memberEntity);
    }

    @Override
    public MemberEntity queryUser(String username, String password) {
        // 查询
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username));
        // 校验用户名
        if (memberEntity == null) {
            return null;
        }
        // 校验密码
        if (!memberEntity.getPassword().equals(DigestUtils.md5Hex( password+memberEntity.getSalt()))) {
            return null;
        }
        // 用户名密码都正确
        return memberEntity;
}

}