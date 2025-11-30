package com.adminpro.rbac.api;

import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.rbac.domains.vo.user.PasswordRuleVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 密码校验工具类
 * 从配置中读取密码规则并校验
 */
public class PasswordValidator {
    private static final Logger logger = LoggerFactory.getLogger(PasswordValidator.class);
    
    private static final String CONFIG_KEY = "app.password.rule";
    
    /**
     * 获取密码规则配置
     */
    public static PasswordRuleVo getPasswordRule() {
        try {
            String configValue = ConfigHelper.getString(CONFIG_KEY, null);
            if (StringUtils.isNotEmpty(configValue)) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(configValue, PasswordRuleVo.class);
            }
        } catch (Exception e) {
            logger.warn("读取密码规则配置失败，使用默认规则", e);
        }
        // 返回默认规则
        return new PasswordRuleVo();
    }
    
    /**
     * 校验密码是否符合规则
     * 
     * @param password 待校验的密码
     * @return 校验结果，如果通过返回null，否则返回错误信息列表
     */
    public static List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();
        
        if (StringUtils.isEmpty(password)) {
            errors.add("密码不能为空");
            return errors;
        }
        
        PasswordRuleVo rule = getPasswordRule();
        
        // 校验长度
        if (password.length() < rule.getMinLength()) {
            errors.add(String.format("密码至少%d位", rule.getMinLength()));
        }
        if (rule.getMaxLength() != null && password.length() > rule.getMaxLength()) {
            errors.add(String.format("密码最多%d位", rule.getMaxLength()));
        }
        
        // 校验小写字母
        if (Boolean.TRUE.equals(rule.getRequireLowerCase()) && !Pattern.compile("[a-z]").matcher(password).find()) {
            errors.add("密码必须包含小写字母");
        }
        
        // 校验大写字母
        if (Boolean.TRUE.equals(rule.getRequireUpperCase()) && !Pattern.compile("[A-Z]").matcher(password).find()) {
            errors.add("密码必须包含大写字母");
        }
        
        // 校验数字
        if (Boolean.TRUE.equals(rule.getRequireDigit()) && !Pattern.compile("\\d").matcher(password).find()) {
            errors.add("密码必须包含数字");
        }
        
        // 校验特殊字符
        if (Boolean.TRUE.equals(rule.getRequireSpecialChar())) {
            String specialChars = rule.getSpecialChars();
            if (StringUtils.isEmpty(specialChars)) {
                specialChars = "@$!%*?&";
            }
            // 转义特殊字符用于正则表达式
            String escapedSpecialChars = Pattern.quote(specialChars);
            String pattern = "[" + escapedSpecialChars + "]";
            if (!Pattern.compile(pattern).matcher(password).find()) {
                errors.add(String.format("密码必须包含特殊字符 (%s)", specialChars));
            }
        }
        
        return errors.isEmpty() ? null : errors;
    }
    
    /**
     * 校验密码是否符合规则（简单版本，返回boolean）
     */
    public static boolean isValidPassword(String password) {
        List<String> errors = validatePassword(password);
        return errors == null || errors.isEmpty();
    }
    
    /**
     * 获取密码规则描述
     */
    public static String getPasswordRuleDescription() {
        PasswordRuleVo rule = getPasswordRule();
        List<String> requirements = new ArrayList<>();
        
        requirements.add(String.format("至少%d位字符", rule.getMinLength()));
        if (rule.getMaxLength() != null) {
            requirements.add(String.format("最多%d位字符", rule.getMaxLength()));
        }
        if (Boolean.TRUE.equals(rule.getRequireLowerCase())) {
            requirements.add("包含小写字母");
        }
        if (Boolean.TRUE.equals(rule.getRequireUpperCase())) {
            requirements.add("包含大写字母");
        }
        if (Boolean.TRUE.equals(rule.getRequireDigit())) {
            requirements.add("包含数字");
        }
        if (Boolean.TRUE.equals(rule.getRequireSpecialChar())) {
            requirements.add(String.format("包含特殊字符 (%s)", rule.getSpecialChars()));
        }
        
        return String.join("、", requirements);
    }
}

