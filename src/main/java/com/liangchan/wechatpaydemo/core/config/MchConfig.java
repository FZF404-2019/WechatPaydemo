package com.liangchan.wechatpaydemo.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * <p>
 * 商户信息
 * </p>
 *
 * @author LiangChan
 * @since 2022-09-22
 */
@Configuration
@ConfigurationProperties(prefix = "wx.mch")
@Data
public class MchConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 发券商户号
     */
    private String mchCode;

    /**
     * 证书序列号
     */
    private String serialNo;

    /**
     * 商户私钥
     */
    private String privateKey;

    /**
     * 平台证书
     */
    private String certificate;

    public void setSerialNo(String cipherText){
        this.serialNo = WxV3ApiHttpConfig.decryptAes(cipherText,WxV3ApiHttpConfig.AES_KEY);
    }

    public void setPrivateKey(String cipherText){
        this.privateKey = WxV3ApiHttpConfig.decryptAes(cipherText,WxV3ApiHttpConfig.AES_KEY);
    }

    public void setCertificate(String cipherText){
        this.certificate = WxV3ApiHttpConfig.decryptAes(cipherText,WxV3ApiHttpConfig.AES_KEY);
    }

}
