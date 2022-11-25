#关于平台证书
微信支付平台的证书可以使用lib目录下的CertificateDownloader-1.1.jar工具包，通过商户私钥和V3接口秘钥生成
具体命令：java -jar CertificateDownloader-1.1.jar -f 商户私钥文件路径 -k v3接口api密钥 -m 商户号 -o 证书保存路径 -s 商户证书序列号
例如：java -jar CertificateDownloader-1.1.jar -f /cert/apiclient_key.pem -k ghjsgwjegrw -m 1234568 -o . -s 32567GHJSGHJSG6435

#关于商户秘钥
配置文件上的秘钥内容是将对应秘钥证书中的内容进行了aes加密再配置到文件中的，项目启动时会将内容加密后再赋值给MchConfig对象属性

