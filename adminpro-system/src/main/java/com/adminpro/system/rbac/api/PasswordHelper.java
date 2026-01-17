package com.adminpro.system.rbac.api;

import com.adminpro.system.core.common.helper.ConfigHelper;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.encrypt.PasswordEncryptExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 密码助手类
 * <p>
 * 提供密码加密、验证和生成等核心功能。
 * 支持登录密码和支付密码两种类型的密码管理。
 * <p>
 * 主要功能：
 * <ul>
 * <li>密码加密：对用户密码进行加密存储</li>
 * <li>密码验证：验证用户输入的密码是否正确</li>
 * <li>密码强度验证：检查密码是否符合安全要求</li>
 * <li>随机密码生成：生成符合安全要求的随机密码</li>
 * </ul>
 * <p>
 * 安全特性：
 * <ul>
 * <li>加密算法：使用可插拔的加密执行器，支持多种加密算法</li>
 * <li>密码强度：要求包含大小写字母、数字和特殊字符的组合</li>
 * <li>随机密码：生成的密码符合复杂度要求，避免弱密码</li>
 * <li>支持配置：密码验证规则可通过配置文件自定义</li>
 * </ul>
 *
 * @author simon
 * @see PasswordEncryptExecutor
 */
public class PasswordHelper {
    /**
     * 日志记录器
     */
    protected static final Logger logger = LoggerFactory.getLogger(PasswordHelper.class);

    /**
     * 大写字母字符集
     */
    private static final char[] CHARS_ALPHA_UPPER = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    /**
     * 小写字母字符集
     */
    private static final char[] CHARS_ALPHA_LOWER = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

    /**
     * 数字字符集
     */
    private static final char[] CHARS_DIGIT = { '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    /**
     * 特殊字符集
     */
    private static final char[] CHARS_SPECIAL = { '*', '@', '$', '_', '#', '&', '^', '!' };

    /**
     * 强密码验证正则表达式（默认）
     * <p>
     * 要求：6-12位，必须包含大写字母、小写字母、数字和特殊字符中的至少三种
     */
    private static final String STRONG_PWD_DEFAULT = "^(?![a-zA-z]{6,12}+$)(?!\\d{6,12}+$)(?![!@#$%^&*]{6,12}+$)(?![a-zA-z\\d]{6,12}+$)(?![a-zA-z!@#$%^&*]{6,12}+$)(?![\\d!@#$%^&*]{6,12}+$)[a-zA-Z\\d!@#$%^&*]{6,12}+$";

    /**
     * 验证密码是否符合安全要求
     * <p>
     * 检查密码是否包含足够复杂度的字符组合。
     * 默认要求包含大小写字母、数字和特殊字符。
     * 可通过配置项app.pwd.validate.reg自定义验证规则。
     *
     * @param pwd 待验证的密码
     * @return true表示密码符合安全要求，false表示不符合
     */
    public static boolean isValidPassword(String pwd) {
        String reg = ConfigHelper.getString("app.pwd.validate.reg", STRONG_PWD_DEFAULT);
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(pwd);
        return matcher.matches();
    }

    /**
     * 加密登录密码
     *
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param pwd        原始密码
     * @return 加密后的密码
     */
    public static String encryptPwd(String userDomain, String loginName, String pwd) {
        return PasswordEncryptExecutor.getInstance().encryptPwd(userDomain, loginName, pwd);
    }

    /**
     * 校验登录密码
     *
     * @param userEntity 用户实体
     * @param pwd        原始密码
     * @return 是否匹配
     */
    public static boolean checkPwd(UserEntity userEntity, String pwd) {
        if (userEntity == null || StringUtils.isEmpty(pwd)) {
            return false;
        }
        return PasswordEncryptExecutor.getInstance().checkPwd(userEntity, pwd);
    }

    /**
     * 加密支付密码
     *
     * @param userDomain 用户域
     * @param loginName  登录名
     * @param pwd        原始密码
     * @return 加密后的密码
     */
    public static String encryptPayPwd(String userDomain, String loginName, String pwd) {
        return PasswordEncryptExecutor.getInstance().encryptPwd(userDomain, loginName, pwd);
    }

    /**
     * 校验支付密码
     *
     * @param userEntity 用户实体
     * @param paypwd     支付密码
     * @return 是否匹配
     */
    public static boolean checkPayPwd(UserEntity userEntity, String paypwd) {
        if (userEntity == null || StringUtils.isEmpty(paypwd)) {
            return false;
        }
        String encryptPwd = encryptPayPwd(userEntity.getUserDomain(), userEntity.getLoginName(), paypwd);
        return StringUtils.equals(encryptPwd, userEntity.getPayPwd());
    }

    /**
     * 生成随机密码
     * <p>
     * 生成符合安全要求的随机密码，包含以下字符类型：
     * <ul>
     * <li>大写字母</li>
     * <li>小写字母</li>
     * <li>数字</li>
     * <li>特殊字符</li>
     * </ul>
     * <p>
     * 算法特点：
     * <ul>
     * <li>确保每种字符类型至少出现一次</li>
     * <li>字符顺序随机打乱</li>
     * <li>密码长度至少为6位</li>
     * </ul>
     *
     * @param length 密码长度，必须大于等于6
     * @return 随机生成的密码
     * @throws IllegalArgumentException 当长度小于6时抛出
     */
    public static String genRandom(int length) {
        Validate.isTrue(length >= 6, "password's length must greater or equal 6");

        Random random = new Random();
        int upperLen = Math.abs(random.nextInt(10000)) % (length / 4);
        upperLen++;
        Stack<Character> upperStack = new Stack<>();
        for (int i = 0; i < upperLen; i++) {
            upperStack.push(CHARS_ALPHA_UPPER[random.nextInt(CHARS_ALPHA_UPPER.length)]);
        }

        int specLen = Math.abs(random.nextInt(10000)) % (length / 4);
        specLen++;
        Stack<Character> specStack = new Stack<>();
        for (int i = 0; i < specLen; i++) {
            specStack.push(CHARS_SPECIAL[random.nextInt(CHARS_SPECIAL.length)]);
        }

        int digitLen = Math.abs(random.nextInt(10000)) % ((length - upperLen - specLen) / 2);
        digitLen++;
        Stack<Character> digitStack = new Stack<>();
        for (int i = 0; i < digitLen; i++) {
            digitStack.push(CHARS_DIGIT[random.nextInt(CHARS_DIGIT.length)]);
        }

        int lowerLen = length - upperLen - specLen - digitLen;
        Stack<Character> lowerStack = new Stack<>();
        for (int i = 0; i < lowerLen; i++) {
            lowerStack.push(CHARS_ALPHA_LOWER[random.nextInt(CHARS_ALPHA_LOWER.length)]);
        }

        List<Stack<Character>> list = new ArrayList<>(Arrays.asList(upperStack, lowerStack, digitStack, specStack));
        StringBuilder builder = new StringBuilder();
        while (list.size() > 0) {
            Stack<Character> stack = list.get(random.nextInt(list.size()));
            builder.append(stack.pop());

            if (stack.isEmpty()) {
                list.remove(stack);
            }
        }

        return builder.toString();
    }
}
