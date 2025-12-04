package com.adminpro.core.base.validator;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseValidator<T> implements IValidator<T> {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public void baseValidate(T model, MessageBundle msgBundle, Class<? extends IValidatorGroup>... groups) {
        ValidationUtil.validate(model, msgBundle, groups);
    }

    public abstract void validate(T model, MessageBundle msgBundle);
}
