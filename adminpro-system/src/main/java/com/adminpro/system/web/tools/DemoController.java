package com.adminpro.system.web.tools;

import com.adminpro.system.core.common.BaseRoutingController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/demo")
public class DemoController extends BaseRoutingController {

    @RequestMapping("/calendar")
    public String config() {
        prepareData();
        return "demo/calendar";
    }
}
