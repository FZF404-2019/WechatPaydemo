package com.liangchan.wechatpaydemo.core.handler;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.http.*;
import com.alibaba.fastjson.JSONObject;
import com.liangchan.wechatpaydemo.core.config.MchConfig;
import com.liangchan.wechatpaydemo.core.config.WxV3ApiHttpConfig;
import com.liangchan.wechatpaydemo.exception.ServiceBusinessException;
import com.liangchan.wechatpaydemo.exception.ServiceException;
import com.liangchan.wechatpaydemo.result.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.security.PrivateKey;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 *
 * @Project: WechatPaydemo
 * @author: LiangChan
 * @DateTime: Created in 2022-01-12 16:43
 * @description:
 */
@Slf4j
@Component
public class WxV3ApiHttpHandler {
    @Autowired
    @Qualifier("wxV3ApiHttpClient")
    private CloseableHttpClient wxV3ApiHttpClient;
    @Autowired
    private WxV3ApiHttpConfig wxV3ApiHttpConfig;

    /**
     * 上送数据到腾讯
     *
     * @param url   接口地址
     * @param body  请求参数
     *
     * @return 响应数据
     */
    public String postForHttpClient(String url, JSONObject body) {
        String param = body.toJSONString();
        log.info("[微信支付] POST请求腾讯API-URL:{}",url);
        log.info("[微信支付] POST请求腾讯API-request:{}",param);

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        httpPost.setEntity(new StringEntity(param, "UTF-8"));
        int httpStatus;
        String resultData;
        try {
            CloseableHttpResponse response = this.wxV3ApiHttpClient.execute(httpPost);
            httpStatus = response.getStatusLine().getStatusCode();
            resultData = EntityUtils.toString(response.getEntity());
            log.info("[微信支付] POST请求腾讯API-response:{}",resultData);
        } catch (Exception e) {
            throw new ServiceException(ResultEnum.HTTP_ERROR,"[微信支付] 请求腾讯接口HTTP异常:" +e.getMessage());
        }
        if (httpStatus == HttpStatus.HTTP_OK) {
            return resultData;
        } else {
            throw new ServiceBusinessException(ResultEnum.OTHER_API_ERROR,"[微信支付] 请求腾讯接口业务异常:" + resultData);
        }
    }

    /**
     * 发出GET请求【平台证书http请求client用】
     *
     * @param url   请求接口URL
     *
     * @return 响应内容
     */
    public String getForHttpClient(String url){
        log.info("[微信支付] GET请求腾讯API-URL:{}",url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");
        int httpStatus;
        String resultData;
        CloseableHttpResponse response;
        try {
            response = this.wxV3ApiHttpClient.execute(httpGet);
            httpStatus = response.getStatusLine().getStatusCode();
            resultData = EntityUtils.toString(response.getEntity());
            log.info("[微信支付] GET请求腾讯API-response:{}",resultData);
        } catch (Exception e){
            throw new ServiceException(ResultEnum.HTTP_ERROR,"[微信支付] 请求腾讯接口HTTP异常:" + e.getMessage());
        }
        if (httpStatus == HttpStatus.HTTP_OK){
            return resultData;
        } else {
            throw new ServiceBusinessException(ResultEnum.OTHER_API_ERROR,"[微信支付] 请求腾讯接口业务异常:" + response);
        }
    }

    /**
     * 下载文件
     *
     * @param url   通过微信业务接口获取的文件下载URL
     * @param file  用于装载内容的文件对象
     */
    public void downloadFile(String url, File file){
        HttpRequest request = this.createGet(url);
        HttpResponse httpResponse;
        try {
            httpResponse = request.execute();
        } catch (Exception e) {
            throw new ServiceBusinessException(ResultEnum.HTTP_ERROR,"[微信支付] 请求腾讯接口HTTP异常:" + e.getMessage());
        }
        httpResponse.writeBody(file);
    }

    /**
     * 发送GET请求
     *
     * @param url   通过微信业务接口获取的文件下载URL
     */
    public String get(String url){
        HttpRequest request = this.createGet(url);
        HttpResponse httpResponse;
        try {
            httpResponse = request.execute();
        } catch (Exception e) {
            throw new ServiceBusinessException(ResultEnum.HTTP_ERROR,"[微信支付] 请求腾讯接口HTTP异常:" + e.getMessage());
        }
        return httpResponse.body();
    }

    /**
     * 创建Http GET请求对象
     *
     * @param url   请求的URL，可以使HTTP或者HTTPS
     */
    private HttpRequest createGet(String url){
        HttpRequest request = HttpUtil.createGet(url);
        String schema = "WECHATPAY2-SHA256-RSA2048 ";
        String token = this.buildToken(Method.GET.name(),url,"");
        String auth = schema + token;
        request.header("Authorization",auth);
        return request;
    }

    /**
     * 创建Http POST请求对象
     *
     * @param url   请求的URL【可以使HTTP或者HTTPS】
     * @param body  请求参数【JSON格式】
     */
    private HttpRequest createPost(String url, String body){
        HttpRequest request = HttpUtil.createPost(url);
        String schema = "WECHATPAY2-SHA256-RSA2048 ";
        String token = this.buildToken(Method.POST.name(),url,body);
        String auth = schema + token;
        request.header("Authorization",auth);
        return request;
    }

    /**
     * 获取含有微信V3平台证书的 Http请求客户端
     * 由WechatPayHttpClientBuilder构建的
     *
     * @return 请求客户端
     */
    public CloseableHttpClient getWxV3ApiHttpClient(){
        return this.wxV3ApiHttpClient;
    }

    /**
     * 获取商户配置
     *
     * @return 发券商户配置
     */
    public MchConfig getMchConfig(){
        return this.wxV3ApiHttpConfig.getMchConfig();
    }

    /**
     * 构建请求头参数Authorization需要用到的token
     *
     * @param method    请求类型【POST请求：POST，GET请求：GET】
     * @param url       请求URL
     * @param body      请求体【POST请求入参json格式字符串，GET请求入参空字符串】
     *
     * @return token内容
     */
    public String buildToken(String method, String url, String body) {
        String nonceStr = RandomUtil.randomString(6);
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = encryptBase64Sha256withRsa(message,this.wxV3ApiHttpConfig.getPrivateKey());
        MchConfig mchConfig = wxV3ApiHttpConfig.getMchConfig();

        return "mchid=\"" + mchConfig.getMchCode() + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + mchConfig.getSerialNo() + "\","
                + "signature=\"" + signature + "\"";
    }

    /**
     * 构建签名内容
     *
     * @param method    请求类型【POST请求：POST，GET请求：GET】
     * @param url       请求URL
     * @param timestamp 请求时间戳
     * @param nonceStr  请求随机数
     * @param body      请求体【POST请求入参json格式字符串，GET请求入参空字符串】
     *
     * @return 需要加签的明文内容
     */
    public String buildMessage(String method, String url, long timestamp, String nonceStr, String body) {
        URL u = URLUtil.url(url);
        String canonicalUrl = URLUtil.encode(u.getPath());
        if (StrUtil.isNotBlank(u.getQuery())) {
            canonicalUrl += "?" + u.getQuery();
        }
        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }

    /**
     * SHA1withRSA加密-Base64编码
     *
     * @param paramStr      需要签名的字符串
     * @param privateKey    私钥
     *
     * @return Base64编码的密文字符串
     */
    public static String encryptBase64Sha256withRsa(String paramStr, PrivateKey privateKey){
        Sign sign = new Sign(SignAlgorithm.SHA256withRSA,privateKey,null);
        byte[] signed = sign.sign(paramStr);
        return Base64.encode(signed);
    }


}
