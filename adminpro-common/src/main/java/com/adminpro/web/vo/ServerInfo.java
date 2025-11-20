package com.adminpro.web.vo;

import lombok.Data;

@Data
public class ServerInfo {
    private String releaseVersion;
    private String buildVersion;
    private String platformName;
    private String platformShortName;
    private String copyRight;
}
