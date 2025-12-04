package com.adminpro.web.tools;

import com.adminpro.framework.common.BaseRoutingController;
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
