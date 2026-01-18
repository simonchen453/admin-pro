package com.adminpro.system.web;

import com.adminpro.framework.base.entity.R;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.tools.domains.entity.session.SessionEntity;
import com.adminpro.system.tools.domains.entity.session.SessionService;
import com.adminpro.system.tools.domains.enums.SessionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "Session管理", description = "Session过期和终止处理接口")
@RestController
@RequestMapping
public class IndexController extends BaseController {

    @Operation(summary = "Session过期处理", description = "检查并处理Session过期状态")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: Session有效
                - restCode=401: Session已过期或被终止
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @RequestMapping(value = "/sessionExpired", method = RequestMethod.GET)
    public R<Void> sessionExpired() throws IOException {
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

    @Operation(summary = "Session终止处理", description = "检查并处理Session终止状态")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: Session有效
                - restCode=401: Session已过期或被终止
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @RequestMapping(value = "/sessionTerminate", method = RequestMethod.GET)
    public R<Void> sessionTerminate() throws IOException {
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
