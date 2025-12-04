package com.adminpro.core.base.validator;

import com.adminpro.core.base.message.MessageBundle;

public interface IValidator<T> {
    void validate(T model, MessageBundle msgBundle);
}
