package com.adminpro.rbac.api;

import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.entity.user.UserIden;
import com.adminpro.rbac.encrypt.PasswordEncryptExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by simon on 2017/6/9.
 */
public class PasswordHelper {
    protected static final Logger logger = LoggerFactory.getLogger(PasswordHelper.class);

    private static final char[] CHARS_ALPHA_UPPER = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] CHARS_ALPHA_LOWER = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] CHARS_DIGIT = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] CHARS_SPECIAL = {'*', '@', '$', '_', '#', '&', '^', '!'};

    private static final String STRONG_PWD_DEFAULT = "^(?![a-zA-z]{6,12}+$)(?!\\d{6,12}+$)(?![!@#$%^&*]{6,12}+$)(?![a-zA-z\\d]{6,12}+$)(?![a-zA-z!@#$%^&*]{6,12}+$)(?![\\d!@#$%^&*]{6,12}+$)[a-zA-Z\\d!@#$%^&*]{6,12}+$";

    public static boolean isValidPassword(String pwd) {
        String reg = ConfigHelper.getString("app.pwd.validate.reg", STRONG_PWD_DEFAULT);
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(pwd);
        return matcher.matches();
    }

    /**
     * 加密登录密码
     *
     * @param userIden
     * @param pwd
     * @return
     */
    public static String encryptPwd(UserIden userIden, String pwd) {
        return PasswordEncryptExecutor.getInstance().encryptPwd(userIden, pwd);
    }

    /**
     * 校验登录密码
     *
     * @param userEntity
     * @param pwd
     * @return
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
     * @param userIden
     * @param pwd
     * @return
     */
    public static String encryptPayPwd(UserIden userIden, String pwd) {
        return PasswordEncryptExecutor.getInstance().encryptPwd(userIden, pwd);
    }

    /**
     * 校验支付密码
     *
     * @param userEntity
     * @param paypwd
     * @return
     */
    public static boolean checkPayPwd(UserEntity userEntity, String paypwd) {
        if (userEntity == null || StringUtils.isEmpty(paypwd)) {
            return false;
        }
        String encryptPwd = encryptPayPwd(userEntity.getUserIden(), paypwd);
        return StringUtils.equals(encryptPwd, userEntity.getPayPwd());
    }

    /**
     * 生成随机密码
     *
     * @param length
     * @return
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
