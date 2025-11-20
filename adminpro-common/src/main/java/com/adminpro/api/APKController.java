package com.adminpro.api;

import com.adminpro.core.base.entity.R;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.rbac.domains.entity.apk.APKEntity;
import com.adminpro.rbac.domains.entity.apk.APKService;
import com.adminpro.rbac.domains.vo.apk.APKVO;
import com.adminpro.rbac.domains.vo.apk.APKVoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/apk")
public class APKController extends BaseRoutingController {

    @Autowired
    private APKService apkService;

    @Autowired
    private APKVoConverter apkVoConverter;

    @RequestMapping(value = "/check-update", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public R checkUpdate(@RequestParam(value = "type", required = true) String type, @RequestParam(value = "verCode", required = true) Integer verCode) throws IOException {
        APKEntity latestVersion = apkService.findLatestVersion(type);

        if (latestVersion != null) {
            if (verCode < latestVersion.getVerCode()) {
                APKVO convert = apkVoConverter.convert(latestVersion);
                return R.ok(convert);
            }
        }
        return R.ok();
    }

    /**
     * APK下载界面
     *
     * @param type
     * @return
     */
    @RequestMapping(value = "/download/page/{type}", method = RequestMethod.GET)
    @ResponseBody
    public String downloadPage(@PathVariable String type) {
        APKEntity latestVersion = apkService.findLatestVersion(type);
        request.setAttribute("apks", latestVersion);
        return "share";
    }
}
