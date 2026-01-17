package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.tools.domains.entity.server.Server;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author simon
 */
@RestController
@RequestMapping(ServerController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:server')")
public class ServerController extends BaseController {

    protected static final String PREFIX_URL = "/api/v1/server";

    @GetMapping("/detail")
    public R detail() throws Exception {
        Server server = new Server();
        server.copyTo();
        return R.ok(server);
    }
}
