package com.adminpro.tools.payment.wxpay;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;

public class WXPayConfigImpl extends BaseWXPayConfig {

    private String certPath;
    private String appID;
    private String mchID;
    private String key;
    private String notifyUrl;
    private String refundNotifyUrl;
    private int httpReadTimeoutMs;
    private int httpConnectTimeoutMs;
    private String primaryDomain;
    private String alternateDomain;
    private int reportWorkerNum;
    private int reportBatchSize;

    private byte[] certData;
    private static WXPayConfigImpl INSTANCE;

    private WXPayConfigImpl() {

    }

    private static void load(WXPayConfigImpl payConfig) throws Exception {
        Resource resource = new ClassPathResource(payConfig.certPath);
        InputStream inputStream = resource.getInputStream();
        File file = File.createTempFile("apiclient_cert", ".p12");
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        InputStream certStream = new FileInputStream(file);
        payConfig.certData = new byte[(int) file.length()];
        certStream.read(payConfig.certData);
        certStream.close();
    }

    public static WXPayConfigImpl getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized (WXPayConfigImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = init();
                }
            }
        }
        return INSTANCE;
    }

    private static WXPayConfigImpl init() throws Exception {
        InputStream resourceAsStream = WXPayConfigImpl.class.getClassLoader().getResourceAsStream("wxpay.json");
        JsonParser parser = new JsonParser();  //创建JSON解析器
        BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"), 50 * 1024 * 1024); //设置缓冲区 编码
        JsonObject object = (JsonObject) parser.parse(in);  //创建JsonObject对象
        String certPath = object.get("CertPath").getAsString();
        String appID = object.get("AppID").getAsString();
        String mchID = object.get("MchID").getAsString();
        String key = object.get("Key").getAsString();
        String notifyUrl = object.get("NotifyUrl").getAsString();
        int httpReadTimeoutMs = object.get("HttpReadTimeoutMs").getAsInt();
        int httpConnectTimeoutMs = object.get("HttpConnectTimeoutMs").getAsInt();
        String primaryDomain = object.get("PrimaryDomain").getAsString();
        String alternateDomain = object.get("AlternateDomain").getAsString();
        int reportWorkerNum = object.get("ReportWorkerNum").getAsInt();
        int reportBatchSize = object.get("ReportBatchSize").getAsInt();
        String refundNotifyUrl = object.get("RefundNotifyUrl").getAsString();
        WXPayConfigImpl payConfig = new WXPayConfigImpl();
        payConfig.setCertPath(certPath);
        payConfig.setAppID(appID);
        payConfig.setMchID(mchID);
        payConfig.setKey(key);
        payConfig.setNotifyUrl(notifyUrl);
        payConfig.setHttpConnectTimeoutMs(httpConnectTimeoutMs);
        payConfig.setHttpReadTimeoutMs(httpReadTimeoutMs);
        payConfig.setPrimaryDomain(primaryDomain);
        payConfig.setAlternateDomain(alternateDomain);
        payConfig.setReportWorkerNum(reportWorkerNum);
        payConfig.setReportBatchSize(reportBatchSize);
        payConfig.setRefundNotifyUrl(refundNotifyUrl);
        load(payConfig);
        return payConfig;
    }

    @Override
    public String getAppID() {
        return appID;
    }

    @Override
    public String getMchID() {
        return mchID;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public String getNotifyUrl() {
        return notifyUrl;
    }

    @Override
    public String getRefundNotifyUrl() {
        return refundNotifyUrl;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return httpConnectTimeoutMs;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return httpReadTimeoutMs;
    }

    @Override
    IWXPayDomain getWXPayDomain() {
        return WXPayDomainSimpleImpl.instance();
    }

    public String getPrimaryDomain() {
        return primaryDomain;
    }

    public String getAlternateDomain() {
        return alternateDomain;
    }

    @Override
    public int getReportWorkerNum() {
        return reportWorkerNum;
    }

    @Override
    public int getReportBatchSize() {
        return reportBatchSize;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public void setMchID(String mchID) {
        this.mchID = mchID;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public void setPrimaryDomain(String primaryDomain) {
        this.primaryDomain = primaryDomain;
    }

    public void setAlternateDomain(String alternateDomain) {
        this.alternateDomain = alternateDomain;
    }

    public void setHttpReadTimeoutMs(int httpReadTimeoutMs) {
        this.httpReadTimeoutMs = httpReadTimeoutMs;
    }

    public void setHttpConnectTimeoutMs(int httpConnectTimeoutMs) {
        this.httpConnectTimeoutMs = httpConnectTimeoutMs;
    }

    public void setReportWorkerNum(int reportWorkerNum) {
        this.reportWorkerNum = reportWorkerNum;
    }

    public void setReportBatchSize(int reportBatchSize) {
        this.reportBatchSize = reportBatchSize;
    }

    public void setRefundNotifyUrl(String refundNotifyUrl) {
        this.refundNotifyUrl = refundNotifyUrl;
    }
}
