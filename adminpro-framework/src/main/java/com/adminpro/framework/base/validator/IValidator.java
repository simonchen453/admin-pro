package com.adminpro.framework.base.validator;

import com.adminpro.framework.base.message.MessageBundle;

public interface IValidator<T> {
    void validate(T model, MessageBundle msgBundle);
}
