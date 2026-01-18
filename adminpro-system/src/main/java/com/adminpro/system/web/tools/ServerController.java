package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.tools.domains.entity.server.Server;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author simon
 */
@Tag(name = "服务器监控", description = "服务器运行状态监控接口")
@RestController
@RequestMapping(ServerController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:server')")
public class ServerController extends BaseController {

    protected static final String PREFIX_URL = "/api/v1/server";

    @Operation(summary = "获取服务器详细信息", description = "获取服务器的CPU、内存、磁盘、JVM等运行状态信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 Server 对象
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @GetMapping("/detail")
    public R detail() throws Exception {
        Server server = new Server();
        server.copyTo();
        return R.ok(server);
    }
}
