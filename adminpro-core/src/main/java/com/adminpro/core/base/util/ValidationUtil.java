
package com.adminpro.core.base.util;

import com.adminpro.core.base.MessageInterpolator;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.IValidatorGroup;
import org.apache.commons.lang3.ArrayUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author simon
 */
public class ValidationUtil {

    public static final String CARDNO_REGEX = "^\\d{16,19}$";
    private static Validator validator = initValidator();
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    public static final String MOBILE_PATTERN = "^((14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$";
    public static final String PASSWORD_PATTERN = "^(?![A-Za-z]+$)(?![A-Z\\d]+$)(?![A-Z\\W]+$)(?![a-z\\d]+$)(?![a-z\\W]+$)(?![\\d\\W]+$)\\S{8,20}$";
    public static final String IDNO_PATTERN = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$)";

    public static <E> void validate(E obj, MessageBundle bundle, Class<? extends IValidatorGroup>... groups) {

        Set<ConstraintViolation<E>> result = null;
        if (ArrayUtils.isNotEmpty(groups)) {
            Class<?>[] clazz = ArrayUtils.add(groups, Default.class);
            result = validator.validate(obj, clazz);
        } else {
            result = validator.validate(obj);
        }

        convertBundle(result, bundle);
    }

    private static <E> void convertBundle(Set<ConstraintViolation<E>> result, MessageBundle bundle) {
        for (ConstraintViolation<E> item : result) {
            bundle.addErrorMessage(item.getPropertyPath().toString(), item.getMessage());
        }
    }

    private static Validator initValidator() {
        ValidatorFactory vf = Validation.byDefaultProvider().configure().messageInterpolator(new MessageInterpolator()).buildValidatorFactory();
        return vf.getValidator();
    }

    public static boolean isValidateEmail(String email) {
        String reg = ConfigUtil.getString("app.email.validate.reg", EMAIL_PATTERN);
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidateMobile(String mobileNo) {
        String reg = ConfigUtil.getString("app.mobileno.validate.reg", MOBILE_PATTERN);
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(mobileNo);
        return matcher.matches();
    }

    public static boolean isValidatePassword(String password) {
        String reg = ConfigUtil.getString("app.password.validate.reg", PASSWORD_PATTERN);
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * 校验是否是合法的身份证号
     *
     * @param idNo
     * @return
     */
    public static boolean isValidateIdNo(String idNo) {
        String reg = ConfigUtil.getString("app.idno.validate.reg", IDNO_PATTERN);
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(idNo);
        return matcher.matches();
    }

    public static boolean isCardNo(String cardNo) {
        Pattern p1 = Pattern.compile(CARDNO_REGEX);
        Matcher matcher = p1.matcher(cardNo);
        return matcher.matches();
    }

    private ValidationUtil() {
    }
}
