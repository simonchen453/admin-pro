package com.adminpro.rbac.domains.vo.user;

import lombok.Data;

/**
 * 密码规则配置VO
 */
@Data
public class PasswordRuleVo {
    /**
     * 最小长度
     */
    private Integer minLength = 8;
    
    /**
     * 最大长度
     */
    private Integer maxLength = 20;
    
    /**
     * 是否需要小写字母
     */
    private Boolean requireLowerCase = true;
    
    /**
     * 是否需要大写字母
     */
    private Boolean requireUpperCase = true;
    
    /**
     * 是否需要数字
     */
    private Boolean requireDigit = true;
    
    /**
     * 是否需要特殊字符
     */
    private Boolean requireSpecialChar = true;
    
    /**
     * 允许的特殊字符（正则表达式字符类）
     */
    private String specialChars = "@$!%*?&";
    
    /**
     * 密码规则描述
     */
    private String description;
}

