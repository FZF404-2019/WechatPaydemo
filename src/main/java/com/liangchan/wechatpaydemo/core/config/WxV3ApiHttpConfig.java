package com.liangchan.wechatpaydemo.core.config;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @Project: WechatPaydemo
 * @author: liangchan
 * @DateTime: Created in 2022/9/22 10:43
 * @description:
 */
@Slf4j
@Service
public class WxV3ApiHttpConfig {
    /**
     * 信息加密key
     */
    public static final String AES_KEY = "RAS5NZ9JI8PJCRIG";

    @Autowired
    private MchConfig config;

    private PrivateKey privateKey;

    /**
     * 证书管理器
     */
    public static ArrayList<X509Certificate> X509CERTIFICATE_LIST;

    @Bean
    public PrivateKey buildPrivateKey(){
        this.privateKey = PemUtil.loadPrivateKey(this.config.getPrivateKey());
        return this.privateKey;
    }

    @Bean
    public X509Certificate buildX509Certificate(){
        return PemUtil.loadCertificate(new ByteArrayInputStream(this.config.getCertificate().getBytes(StandardCharsets.UTF_8)));
    }

    @Bean("wxV3ApiHttpClient")
    public CloseableHttpClient initWxV3ApiHttpClient(PrivateKey privateKey, X509Certificate wechatPayCertificate){

        ArrayList<X509Certificate> listCertificates = this.initX509Certificate(wechatPayCertificate);

        CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
                .withMerchant(config.getMchCode(), config.getSerialNo(), privateKey)
                .withWechatPay(listCertificates)
                .build();
        log.info("[微信支付] 商户加载完毕:{}",this.config.getMchCode());
        return httpClient;
    }

    /**
     * 初始化证书管理器
     *
     * @param wechatPayCertificate 平台证书
     * @return 管理器
     */
    public ArrayList<X509Certificate> initX509Certificate(X509Certificate wechatPayCertificate){
        ArrayList<X509Certificate> listCertificates = new ArrayList<>();
        listCertificates.add(wechatPayCertificate);
        X509CERTIFICATE_LIST = listCertificates;
        return listCertificates;
    }

    public MchConfig getMchConfig(){
        return this.config;
    }

    public PrivateKey getPrivateKey(){
        return this.privateKey;
    }

    /**
     * 敏感信息16进制AES加密
     *
     * @param content 加密内容
     *
     * @return 密文 16进制表示
     */
    public static String encryptAesHex(String content, String key){
        AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
        return aes.encryptHex(content);
    }

    /**
     * 敏感信息AES解密
     *
     * @param cipherText 密文
     * @return 明文
     */
    public static String decryptAes(String cipherText, String key){
        AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
        return aes.decryptStr(cipherText);
    }

}
