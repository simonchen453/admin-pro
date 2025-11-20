package com.adminpro.framework.common.web;

import com.adminpro.core.base.web.AbsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础Controller控制器父类
 *
 * @author simon
 */
public abstract class BaseController extends AbsController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
}
