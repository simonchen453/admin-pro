package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.core.security.jwt.DeviceFingerprintService;
import com.adminpro.system.core.security.jwt.RefreshTokenService;
import com.adminpro.system.rbac.api.LoginHelper;
import com.adminpro.system.rbac.domains.entity.device.UserDeviceDao;
import com.adminpro.system.rbac.domains.entity.device.UserDeviceEntity;
import com.adminpro.system.rbac.domains.vo.device.DeviceListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备管理控制器
 * <p>
 * 提供用户设备查询、踢出等管理功能
 * </p>
 *
 * @author adminpro
 * @since 1.0.0
 */
@Slf4j
@Tag(name = "设备管理", description = "用户设备查询、踢出等管理功能")
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceManagementController extends BaseController {

    private final UserDeviceDao userDeviceDao;
    private final RefreshTokenService refreshTokenService;
    private final DeviceFingerprintService fingerprintService;

    /**
     * 获取当前用户的设备列表
     */
    @Operation(summary = "获取设备列表", description = "获取当前登录用户的所有活跃设备")
    @GetMapping("/list")
    public R<List<DeviceListVo>> listDevices(HttpServletRequest request) {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser == null) {
            return R.error("未登录");
        }

        String userId = loginUser.getUserId();

        // 获取当前请求的设备指纹
        String currentDeviceId = fingerprintService.generateFingerprint(request);

        // 获取用户的所有设备
        List<UserDeviceEntity> devices = userDeviceDao.findByUserId(userId);

        // 转换为 VO
        List<DeviceListVo> result = devices.stream()
                .filter(d -> d.getIsActive() != null && d.getIsActive() == 1)
                .map(d -> {
                    DeviceListVo vo = new DeviceListVo();
                    vo.setId(d.getDeviceId());
                    vo.setPlatform(d.getPlatform());
                    vo.setDeviceName(d.getDeviceName());
                    vo.setIp(d.getLastIp());
                    vo.setLastActiveAt(d.getLastActiveAt());
                    vo.setIsCurrent(d.getDeviceId().equals(currentDeviceId));
                    return vo;
                })
                .collect(Collectors.toList());

        return R.ok(result);
    }

    /**
     * 踢出指定设备
     */
    @SysLog("踢出设备")
    @Operation(summary = "踢出设备", description = "踢出指定设备，该设备需要重新登录")
    @DeleteMapping("/{deviceId}")
    public R<String> revokeDevice(@PathVariable String deviceId, HttpServletRequest request) {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser == null) {
            return R.error("未登录");
        }

        String userId = loginUser.getUserId();

        // 查找设备
        UserDeviceEntity device = userDeviceDao.findByDeviceId(userId, deviceId);
        if (device == null) {
            return R.error("设备不存在");
        }

        // 不能踢出当前设备
        String currentDeviceId = fingerprintService.generateFingerprint(request);
        if (deviceId.equals(currentDeviceId)) {
            return R.error("不能踢出当前设备，请使用登出功能");
        }

        // 撤销 Refresh Token
        if (device.getRefreshTokenJti() != null) {
            refreshTokenService.revokeRefreshToken(device.getRefreshTokenJti());
        }

        // 标记为不活跃
        device.setIsActive(0);
        device.setRefreshTokenJti(null);
        userDeviceDao.update(device);

        log.info("用户 {} 踢出设备: {} ({})", userId, deviceId, device.getDeviceName());

        return R.ok("设备已移除");
    }

    /**
     * 踢出所有其他设备（保留当前设备）
     */
    @SysLog("踢出所有其他设备")
    @Operation(summary = "踢出所有其他设备", description = "踢出除当前设备外的所有设备")
    @DeleteMapping("/revoke-others")
    public R<String> revokeOtherDevices(HttpServletRequest request) {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser == null) {
            return R.error("未登录");
        }

        String userId = loginUser.getUserId();
        String currentDeviceId = fingerprintService.generateFingerprint(request);

        // 获取用户的所有设备
        List<UserDeviceEntity> devices = userDeviceDao.findByUserId(userId);

        int revokedCount = 0;
        for (UserDeviceEntity device : devices) {
            // 跳过当前设备和不活跃的设备
            if (device.getIsActive() == null || device.getIsActive() != 1) {
                continue;
            }
            if (device.getDeviceId().equals(currentDeviceId)) {
                continue;
            }

            // 撤销 Refresh Token
            if (device.getRefreshTokenJti() != null) {
                refreshTokenService.revokeRefreshToken(device.getRefreshTokenJti());
            }

            // 标记为不活跃
            device.setIsActive(0);
            device.setRefreshTokenJti(null);
            userDeviceDao.update(device);

            revokedCount++;
        }

        log.info("用户 {} 踢出了 {} 个其他设备", userId, revokedCount);

        return R.ok(String.format("已踢出 %d 个设备", revokedCount));
    }
}
