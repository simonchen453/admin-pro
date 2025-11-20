package com.adminpro.tools.payment.alipay;

import com.adminpro.tools.payment.alipay.sign.RSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AliPaySubmit {

    @Autowired
    AliPayConfig aliPayConfig;

    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    public static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";

    /**
     * 生成签名结果
     *
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
    public String buildRequestSign(Map<String, String> sPara) {
        String prestr = AliPayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        if ("RSA".equals(aliPayConfig.getSignType())) {
            mysign = RSA.sign(prestr, aliPayConfig.getAppPrivateKey(), aliPayConfig.getInputCharset());
        }
        return mysign;
    }

    /**
     * 生成要请求给支付宝的参数数组
     *
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    public Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
        //除去数组中的空值和签名参数
        Map<String, String> sPara = AliPayCore.paraFilter(sParaTemp);
        //生成签名结果
        String sign = buildRequestSign(sPara);
        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", sign);
        sPara.put("sign_type", aliPayConfig.getSignType());
        return sPara;
    }

}
