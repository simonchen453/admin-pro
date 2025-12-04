package com.adminpro.tools.payment.alipay;


import org.springframework.stereotype.Component;

@Component
public class AliPayConfig {

    //    @Value("${app.server.base-path}")
    private String serverPath;

    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public String partner = "2088621873769180";

    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
    public String sellerId = partner;

    //商户的私钥,需要PKCS8格式，RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
    public String appPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMttv2EUUtC+EYro" +
            "mbcsT9ZabM9m7VgoCzrvGL4QFayLNIGeFTBHU6l0BfVi3YuwWygqT10FjUhStlYU" +
            "+wzsrFSooflcytIxn2lBsZqg67TTN3Ag3nTHEZuP6TCEjd/iWI8maVtkZSSZ4TiO" +
            "chmByzY7xR6/k+esn7u4rgx1pod7AgMBAAECgYAjzUbXQQpqLmlL9y/GawgKA5QO" +
            "1GCFGVcQoi/Kk24dFcrB3R6BhqwUsJSz4Rh4kysX4IpjJqz8w4HPmJWuaqDRXRri" +
            "JpPMO6La6tc4PjgRhEOtzBTlhg6XRGrgLZXyTuCo0RylzienF6R7v2bnY0LNvf1j" +
            "rg6k5aZ92XjAo8YHAQJBAOnIH81J47fW9skdEfJsltpcGYN71gAQ627RU7oTg6ZC" +
            "DIh297TOW5PXmDqrFoPW33ZnGVE8jy4zhG8JuiD7ibsCQQDewyNwNaCVPI92+qcD" +
            "RoZqYZLnA4AJuBSEqG3ZTTeu3Dt2zmmpKhbxzhFZFvxfyW96lqu66FknNX5bj/Gi" +
            "9T1BAkEA4nnmHRlEyrcsMp6/4StDZNyuNstEAKJjgeK2CuCWu/zmaVlfFMCIxQuq" +
            "RMOztxr1Np4gT6usRaTQ2kUBFKy5uwJAUgg+VqYI+qwQkNoBAt3HxgkkldneHrYh" +
            "KDD2Mt2Ssdv8MeYVVuxfArDGBq7GRWfim5w8pVnG6v9yBrndXAs/gQJAewSFKmJY" +
            "rqOISdx0GnyOhIZ3+n+pw+Aa9nBV4pVlnWLGUdifnsYwpMePzkY1NlPbdiYyTdLA" +
            "PUgLHvJjyHjJ7Q==";
    // 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
    public String appAlipayPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLbb9hFFLQvhGK6Jm3LE/WWmzPZu1YKAs67xi+EBWsizSBnhUwR1OpdAX1Yt2LsFsoKk9dBY1IUrZWFPsM7KxUqKH5XMrSMZ9pQbGaoOu00zdwIN50xxGbj+kwhI3f4liPJmlbZGUkmeE4jnIZgcs2O8Uev5PnrJ+7uK4MdaaHewIDAQAB";

    //商户的私钥,需要PKCS8格式，RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
    public String webPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDGnI6Y1KZwPlgs" +
            "hDOCCBfmuoS/rPk/djuoyhUDhWF1s2Za0WqFaQ/uag3oJf1Qga4I4Jykj+u1vHRF" +
            "vxVIcH21OgbyOM0692d5Mulx2CtgZVQZwxI28HpMLrQVF+31jAqRDtkahe/A7JKz" +
            "p4HP5z2vNQYvLgBc9tT+2KMfoUeHnSe0p1i3bPyKIvbpkCx85S5X0sMuKiagvbJA" +
            "pcgHd7IZXuUEmZBz8v++54HOWaSIDaXxUzHbk/A+CKVinGg3oFz+NjRMK+NsdVOc" +
            "g6FJbo8Bo2bVh+4p3SumO21zWn36xe9lihh6Gx+cpQbgLheEKnEesIDc+xNH0Ftd" +
            "ivaBnHWlAgMBAAECggEAc7PSsp/3Uh5PuadOZmsWmXiBSzSWg2z6dms7cLCsRSg8" +
            "8Z1cA9pfyJGKBnrTJqpVW6CWQJwDQf1GczdNS8UUxpXGZCfJD5f/dLC/JG49XsvJ" +
            "AbUNkGnppSX0DPHC07BAm57dxUDPmkF3eSeVTSu2WLJ4JtYSqhW1ob1M0mlfRVW1" +
            "eO7VGe2vwyFGNhGdJ8G/YNgvSUmmN9K7nmVlpcttRreGdSYqyFD0ZbbLSYNt2dvz" +
            "f1e11xCkr01ARm7kCRXeM5xwUj9sW7cXMeK7IWXyu/FhlTFZfKPtQVlKjnj/oQOV" +
            "HSCwtxNkhFrJ0izcZqM/T8RLLtFn9vC9dOR0NRgHxQKBgQD2vckGjEC3BmS18JM9" +
            "wqgB6y23z1NjrMUh6ZEhjz7gFLNUpVfcR22qOyLqMOOAo2NhsQVfwGaXtIbKo7RB" +
            "/WbWfq4KKiJ5A4GVmWZGgcbmdLnJH5PnuU/5cGb37rfJQ+Kpe/PVtrQvV659/qdk" +
            "+b+JGPYxth5NfzcNVhCkY6drwwKBgQDOEG78lZfu52/LFc7yRafCZ1LbceAlP7h3" +
            "Hu5E4wCu+cFeR4CgW0xoiHXYf/Jkk6/iGrg5qC3BrQnwxJXHYAJtJiqpBMUhyM61" +
            "rzSg4es7q9RpFzvJdk1IRoJJaZpbcMa1NU+MV3XM1XPa0ccT0uyE8E6fg3iw/rZV" +
            "/aYErKFKdwKBgQDQ1g8IULsRbgLdjxJAirnCbfgTBBJzYa60JkklG2W/KSYjdZJo" +
            "qbgjBcxmZRyZKzEZZUfEJEdxDWWsq2QNGlnpH+1UQxdMT3BlWyGaeYsMUS1SEj0Q" +
            "OuN6nu032b+KRL/abUQS1YGqNE9G6rtplozLj+oIZzk8pHjG+9o4GPSCGwKBgAO3" +
            "bCkGEa1vYybIh+j6tKHe9zGaBi7lzU/osOdxKKv/OSuYIovB2cjw3R9Af4Cs4Tk0" +
            "vincDKc3lbxP71ufMawCSjauLVgEpSXFuS9Fo9Z5rOUQVDW3+oDZNLRaQTxw9kSC" +
            "Z6LiWO+O0f4aVnrXDQkAxIo/9sdKcMNwRbRD4MzVAoGAN+8BBPnh+wj/7cJs1cus" +
            "ywOOlupINuYBVjb73IFcIBD6AiT31+roE8ktNB9TwgXndJzoCcX4kHhDtCCyfox9" +
            "wS6TU0Hd8VwqErQQU6ow3yl8kNEjoXBcYOcfFVvMdkLlDAcCvgqtzQ4HohETKJNo" +
            "XCaO8vj/JijFNZZOCUvp8NE=";
    // 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
    public String webAlipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoChZkECF/AeILp9TkhlDkX3G6RBlzTg3b82xv3fUGoA9hpeQioMrAsoR5txfnVvagdZFO/x7PqCjXQaExiARhbmIAxpz6OXAiC2iW19g8hYtfCf5ZY012tHxhvdL/ueGx5zcAG/PIzu1iE9XXZ8kNkdW+h98XISUEK79nkR33kOFnto/eXKBgAZ3NlcSYNAdi/LIfmA7uUUgD1QSdC/D6wXdxcFfaHZNiZBNzMgSWvM0nmvgYD7bH9McEk9Nk47CKWU1Nae2A0m7NqhttzrtYUiNrx7lSP5tZDPzoyV0t/BQHFnLdxYtAL2/XyzkqpmFem87KAsViCVg5xDLjlGbMwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public String notifyUrl = "api/v1/frontend/payment/alipay/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public String returnUrl = "order/payment/return";

    // 签名方式
    public String signType = "RSA2";

    public String appId = "2018032402439780";


    // 字符编码格式 目前支持 gbk 或 utf-8
    public String inputCharset = "utf-8";

    // 支付类型 ，无需修改
    public String paymentType = "1";

    // 调用的接口名，无需修改
    public String service = "create_direct_pay_by_user";


    /**
     * ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
     **/

    /**
     * ↓↓↓↓↓↓↓↓↓↓ 请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     **/

    // 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
    public String antiPhishingKey = "";

    // 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
    public String exterInvokeIp = "";

    /**
     * ↑↑↑↑↑↑↑↑↑↑请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
     */


    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getAppPrivateKey() {
        return appPrivateKey;
    }

    public void setAppPrivateKey(String appPrivateKey) {
        this.appPrivateKey = appPrivateKey;
    }

    public String getAppAlipayPublicKey() {
        return appAlipayPublicKey;
    }

    public void setAppAlipayPublicKey(String appAlipayPublicKey) {
        this.appAlipayPublicKey = appAlipayPublicKey;
    }

    public String getNotifyUrl() {
        return serverPath + notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return serverPath + returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAntiPhishingKey() {
        return antiPhishingKey;
    }

    public void setAntiPhishingKey(String antiPhishingKey) {
        this.antiPhishingKey = antiPhishingKey;
    }

    public String getExterInvokeIp() {
        return exterInvokeIp;
    }

    public void setExterInvokeIp(String exterInvokeIp) {
        this.exterInvokeIp = exterInvokeIp;
    }

    public String getAppId() {
        return appId;
    }

    public String getWebPrivateKey() {
        return webPrivateKey;
    }

    public String getWebAlipayPublicKey() {
        return webAlipayPublicKey;
    }
}
