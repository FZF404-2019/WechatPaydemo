package com.liangchan.wechatpaydemo.core.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.liangchan.wechatpaydemo.core.handler.WxV3ApiHttpHandler;
import com.liangchan.wechatpaydemo.result.ResultBean;
import com.liangchan.wechatpaydemo.result.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @Project: WechatPaydemo
 * @author: liangchan
 * @DateTime: Created in 2022/11/25 22:10
 * @description:
 */
@RestController
public class DemoController {

    @Autowired
    private WxV3ApiHttpHandler httpHandler;

    /**
     * 下载代金券批次核销明细
     */
    @GetMapping("/test")
    public ResultBean<?> checkServer(){
        String url = "https://api.mch.weixin.qq.com/v3/marketing/favor/stocks/代金券批次号/use-flow";

        try {
            String resultData = this.httpHandler.getForHttpClient(url);
            JSONObject data = JSONObject.parseObject(resultData);
            url = data.getString("url");
            //取文件
            /*File file = new File("test.csv");
            this.httpHandler.downloadFile(url,file);*/
            //取数据内容
            String flow = this.httpHandler.get(url);
            System.out.println(flow);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultBean<>(ResultEnum.SUCCESS);
    }


}
