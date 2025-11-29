package com.adminpro.web;

import com.adminpro.core.base.entity.R;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.tools.domains.entity.session.SessionEntity;
import com.adminpro.tools.domains.entity.session.SessionService;
import com.adminpro.tools.domains.enums.SessionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping
public class IndexController extends BaseRoutingController {

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/sessionExpired", method = RequestMethod.GET)
    public R sessionExpired() throws IOException {
        SessionEntity sessionEntity = SessionService.getInstance().findBySessionId(WebHelper.getSessionId());
        if (sessionEntity == null) {
            return R.error("Session not found");
        } else {
            if (StringHelper.equals(sessionEntity.getStatus(), SessionStatus.EXPIRE.getCode())) {
                return R.error("401", "Session expired");
            } else if (StringHelper.equals(sessionEntity.getStatus(), SessionStatus.KILLED.getCode())) {
                return R.error("401", "Session killed");
            } else {
                return R.ok();
            }
        }
    }

    @RequestMapping(value = "/sessionTerminate", method = RequestMethod.GET)
    public R sessionTerminate() throws IOException {
        SessionEntity sessionEntity = SessionService.getInstance().findBySessionId(WebHelper.getSessionId());
        if (sessionEntity == null) {
            return R.error("Session not found");
        } else {
            if (StringHelper.equals(sessionEntity.getStatus(), SessionStatus.EXPIRE.getCode())) {
                 return R.error("401", "Session expired");
            } else if (StringHelper.equals(sessionEntity.getStatus(), SessionStatus.KILLED.getCode())) {
                 return R.error("401", "Session terminated");
            } else {
                return R.ok();
            }
        }
    }
}
