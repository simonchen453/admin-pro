package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.core.security.jwt.RefreshTokenService;
import com.adminpro.system.rbac.domains.entity.device.UserDeviceDao;
import com.adminpro.system.rbac.domains.entity.device.UserDeviceEntity;
import com.adminpro.system.rbac.domains.vo.device.DeviceSearchForm;
import com.adminpro.system.rbac.domains.vo.device.UserDeviceVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@PreAuthorize("@ss.hasPermission('system:device:list')")
public class DeviceManagementController extends BaseController {

    private final UserDeviceDao userDeviceDao;
    private final RefreshTokenService refreshTokenService;

    /**
     * 管理员获取所有设备列表
     */
    @Operation(summary = "管理员获取设备列表", description = "获取系统所有设备（支持搜索）")
    @PostMapping("/search")
    public R<QueryResultSet<UserDeviceVo>> listAll(@RequestBody DeviceSearchForm searchForm) {
        QueryResultSet<UserDeviceVo> result = userDeviceDao.searchDevices(searchForm);
        return R.ok(result);
    }

    /**
     * 管理员踢出设备
     */
    @SysLog("管理员踢出设备")
    @Operation(summary = "管理员踢出设备", description = "管理员强制踢出用户的设备")
    @DeleteMapping("/{userId}/{deviceId}")
    public R<String> kickoutDevice(@PathVariable String userId, @PathVariable String deviceId) {
        UserDeviceEntity device = userDeviceDao.findByDeviceId(userId, deviceId);
        if (device == null) {
            return R.error("设备不存在");
        }

        // 撤销 Refresh Token
        if (device.getRefreshTokenJti() != null) {
            refreshTokenService.revokeRefreshToken(device.getRefreshTokenJti());
        }

        // 标记为不活跃
        device.setIsActive(0);
        device.setRefreshTokenJti(null);
        userDeviceDao.update(device);

        log.info("管理员踢出用户 {} 的设备: {} ({})", userId, deviceId, device.getDeviceName());

        return R.ok("设备已强制下线");
    }
}
