package com.adminpro.system.web.vo;

import lombok.Data;

@Data
public class ReleaseInfo {
    private String releaseVersion;
    private String buildVersion;
    private String platformName;
    private String platformShortName;
    private String copyRight;
}
