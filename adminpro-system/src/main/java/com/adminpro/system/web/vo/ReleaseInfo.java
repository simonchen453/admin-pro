package com.adminpro.system.web.vo;

import lombok.Data;

@Data
public class ReleaseInfo {
    private String releaseVersion;
    private String buildVersion;
    private String platformName;
    private String platformShortName;
    private String copyRight;
    private SysInfo sys;
    private JvmInfo jvm;

    @Data
    public static class SysInfo {
        private String computerName;
        private String osName;
        private String osArch;
        private String userDir;
    }

    @Data
    public static class JvmInfo {
        private String version;
        private String home;
    }
}
