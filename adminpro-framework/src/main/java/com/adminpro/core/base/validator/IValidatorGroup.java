package com.adminpro.core.base.validator;

/**
 * 验证组常量
 */
public interface IValidatorGroup {
    static interface Create extends IValidatorGroup {
    }

    static interface Update extends IValidatorGroup {
    }

    static interface Delete extends IValidatorGroup {
    }
}
