package com.atguigu.gmall.gateway.config;

import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PublicKey;

/**
 * @author kaixuan
 * @version 1.0
 * @date 4/4/2020 上午11:22
 */
@ConfigurationProperties("gmall.jwt")
@Data
@Slf4j
public class JwtProperties {



    private String pubKeyPath;// 公钥

    private PublicKey publicKey; // 公钥
    private String cookieName; // cookie名称

    /**
     * @PostContruct：在构造方法执行之后执行该方法
     */
    @PostConstruct
    public void init() {
        try {
            File pubKey = new File(pubKeyPath);
            // 获取公钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("获取公钥失败！", e);
            throw new RuntimeException(e);
        }
    }


}
