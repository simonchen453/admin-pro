package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.core.security.jwt.DeviceFingerprintService;
import com.adminpro.system.core.security.jwt.RefreshTokenService;
import com.adminpro.system.rbac.api.LoginHelper;
import com.adminpro.system.rbac.domains.entity.device.UserDeviceDao;
import com.adminpro.system.rbac.domains.entity.device.UserDeviceEntity;
import com.adminpro.system.rbac.domains.vo.device.UserDeviceVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户设备管理控制器
 *
 * @author adminpro
 * @since 1.0.0
 */
@Tag(name = "设备管理", description = "用户在线设备管理接口")
@RestController
@RequestMapping("/api/v1/auth/devices")
@RequiredArgsConstructor
public class UserDeviceController extends BaseController {

    private final UserDeviceDao userDeviceDao;
    private final RefreshTokenService refreshTokenService;
    private final DeviceFingerprintService deviceFingerprintService;

    @Operation(summary = "获取在线设备列表", description = "获取当前用户的所有活跃登录设备")
    @GetMapping
    public R<List<UserDeviceVo>> list(HttpServletRequest request) {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser == null) {
            return R.error("未登录");
        }

        List<UserDeviceEntity> entities = userDeviceDao.findByUserId(loginUser.getUserId());
        String currentDeviceId = deviceFingerprintService.getDeviceId(request);

        List<UserDeviceVo> list = entities.stream()
                .filter(e -> e.getIsActive() != null && e.getIsActive() == 1) // 仅显示活跃设备
                .map(e -> {
                    UserDeviceVo vo = new UserDeviceVo();
                    vo.setDeviceId(e.getDeviceId());
                    vo.setDeviceName(e.getDeviceName());
                    vo.setPlatform(e.getPlatform());
                    vo.setLastIp(e.getLastIp());
                    vo.setLastActiveAt(e.getLastActiveAt());
                    // 判断是否为当前设备
                    boolean isCurrent = StringUtils.equals(e.getDeviceId(), currentDeviceId);
                    vo.setIsCurrent(isCurrent);
                    return vo;
                })
                .sorted(Comparator.comparing(UserDeviceVo::getIsCurrent).reversed() // 当前设备排前面
                        .thenComparing(UserDeviceVo::getLastActiveAt, Comparator.reverseOrder())) // 按活跃时间倒序
                .collect(Collectors.toList());

        return R.ok(list);
    }

    @Operation(summary = "踢出设备", description = "强制下线指定设备（撤销其 Refresh Token）")
    @DeleteMapping("/{deviceId}")
    public R<String> kickout(@PathVariable String deviceId, HttpServletRequest request) {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser == null) {
            return R.error("未登录");
        }

        // 防止踢出当前设备
        String currentDeviceId = deviceFingerprintService.getDeviceId(request);
        if (StringUtils.equals(deviceId, currentDeviceId)) {
            return R.error("不能踢出当前设备，请使用退出登录功能");
        }

        refreshTokenService.revokeByDeviceId(loginUser.getUserId(), deviceId);
        return R.ok("设备已下线");
    }
}
