package com.adminpro.web.tools;

import com.adminpro.core.base.entity.R;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.tools.domains.entity.server.Server;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 服务器监控
 *
 * @author simon
 */
@Controller
@RequestMapping("/admin/server")
@PreAuthorize("@ss.hasPermission('system:server')")
public class ServerController extends BaseRoutingController {

    protected static final String PREFIX = "admin/server";

    @GetMapping()
    public String index() throws Exception {
        prepareData();
        return PREFIX + "/list";
    }

    @GetMapping("/detail")
    @ResponseBody
    public R detail() throws Exception {
        Server server = new Server();
        server.copyTo();
        return R.ok(server);
    }
}
