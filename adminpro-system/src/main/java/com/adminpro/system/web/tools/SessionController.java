package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.tools.domains.entity.session.SessionEntity;
import com.adminpro.system.tools.domains.entity.session.SessionService;
import com.adminpro.system.tools.domains.enums.SessionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户会话管理控制器
 * <p>
 * 提供用户会话的查询和管理功能，包括：
 * <ul>
 * <li>用户会话列表查询（支持分页和多条件筛选）</li>
 * <li>用户会话详情查看</li>
 * <li>用户会话的状态管理（暂停、恢复、终止）</li>
 * </ul>
 * </p>
 *
 * @author simon
 * @date 2020-06-17
 */
@Tag(name = "用户会话管理", description = "用户会话管理接口，提供会话查询和状态管理功能")
@RestController
@RequestMapping(SessionController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:session')")
public class SessionController extends BaseController {

    protected static final String PREFIX_URL = "/api/v1/tools/sessions";
    protected static final String SEARCH_FORM_KEY = "sessionSearchForm";

    @Autowired
    private SessionService sessionService;

    /**
     * 查询用户会话列表
     * <p>
     * 支持按Session ID、状态、用户登录名、登录IP、部门编号等条件进行分页查询
     * </p>
     *
     * @param searchForm 搜索表单，包含分页信息和筛选条件
     * @return 包含用户会话列表和分页信息的查询结果集
     */
    @Operation(summary = "查询用户会话列表", description = "支持按Session ID、状态、用户登录名、登录IP、部门编号等条件进行分页查询")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "搜索条件", required = true, content = @Content(schema = @Schema(implementation = SearchForm.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = R.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping("/search")
    public R<QueryResultSet<SessionEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String sessionId = searchForm.getSessionId();
        String status = searchForm.getStatus();
        String loginName = searchForm.getLoginName();
        String ipAddr = searchForm.getIpAddr();
        String deptNo = searchForm.getDeptNo();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(sessionId)) {
            param.addFilter("sessionId", sessionId);
        }
        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", status);
        }
        if (StringUtils.isNotEmpty(loginName)) {
            param.addFilter("loginName", loginName);
        }
        if (StringUtils.isNotEmpty(ipAddr)) {
            param.addFilter("ipAddr", ipAddr);
        }
        if (StringUtils.isNotEmpty(deptNo)) {
            param.addFilter("deptNo", deptNo);
        }
        QueryResultSet<SessionEntity> resultSet = sessionService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 获取会话详细信息
     * <p>
     * 根据会话ID查询用户会话的详细信息
     * </p>
     *
     * @param id 会话ID
     * @return 会话实体对象，不包含审计时间字段
     */
    @Operation(summary = "获取会话详细信息", description = "根据会话ID查询用户会话的详细信息")
    @Parameter(name = "id", description = "会话ID", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = SessionEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @GetMapping("/{id}")
    public R<SessionEntity> detail(@PathVariable String id) {
        SessionEntity entity = sessionService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 暂停会话
     * <p>
     * 将指定用户会话的状态设置为暂停状态，不允许操作当前用户自己的会话
     * </p>
     *
     * @param id 会话ID
     * @return 会话实体对象
     */
    @Operation(summary = "暂停会话", description = "将指定用户会话的状态设置为暂停状态，不允许操作当前用户自己的会话")
    @Parameter(name = "id", description = "会话ID", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "暂停成功", content = @Content(schema = @Schema(implementation = SessionEntity.class))),
            @ApiResponse(responseCode = "400", description = "不能操作当前用户"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @SysLog("暂停会话")
    @PatchMapping("/{id}/suspend")
    public R<SessionEntity> suspend(@PathVariable String id) {
        SessionEntity entity = sessionService.findById(id);
        if (StringHelper.equals(WebHelper.getSessionId(), entity.getSessionId())) {
            return R.error("不能操作当前用户");
        }
        if (entity != null) {
            entity.setStatus(SessionStatus.SUSPEND.getCode());
            sessionService.update(entity);
            return R.ok();
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 恢复会话
     * <p>
     * 将已暂停的用户会话状态恢复为激活状态，不允许操作当前用户自己的会话
     * </p>
     *
     * @param id 会话ID
     * @return 会话实体对象
     */
    @Operation(summary = "恢复会话", description = "将已暂停的用户会话状态恢复为激活状态，不允许操作当前用户自己的会话")
    @Parameter(name = "id", description = "会话ID", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "恢复成功", content = @Content(schema = @Schema(implementation = SessionEntity.class))),
            @ApiResponse(responseCode = "400", description = "不能操作当前用户"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @SysLog("恢复会话")
    @PatchMapping("/{id}/unsuspend")
    public R<SessionEntity> unsuspend(@PathVariable String id) {
        SessionEntity entity = sessionService.findById(id);
        if (StringHelper.equals(WebHelper.getSessionId(), entity.getSessionId())) {
            return R.error("不能操作当前用户");
        }
        if (entity != null) {
            entity.setStatus(SessionStatus.ACTIVE.getCode());
            sessionService.update(entity);
            return R.ok();
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 终止会话
     * <p>
     * 终止指定的用户会话，不允许操作当前用户自己的会话
     * </p>
     *
     * @param id 会话ID
     * @return 会话实体对象
     */
    @Operation(summary = "终止会话", description = "终止指定的用户会话，不允许操作当前用户自己的会话")
    @Parameter(name = "id", description = "会话ID", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "终止成功", content = @Content(schema = @Schema(implementation = SessionEntity.class))),
            @ApiResponse(responseCode = "400", description = "不能操作当前用户"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @SysLog("终止会话")
    @PatchMapping("/{id}/kill")
    public R<SessionEntity> kill(@PathVariable String id) {
        SessionEntity entity = sessionService.findById(id);
        if (StringHelper.equals(WebHelper.getSessionId(), entity.getSessionId())) {
            return R.error("不能操作当前用户");
        }
        if (entity != null) {
            entity.setStatus(SessionStatus.KILLED.getCode());
            sessionService.update(entity);
            return R.ok();
        } else {
            return R.error("对象不存在");
        }
    }

    @Data
    @lombok.EqualsAndHashCode(callSuper = false)
    public static class SearchForm extends BaseSearchForm {
        /**
         * Session ID
         */
        private String sessionId;
        /**
         * 状态
         */
        private String status;
        /**
         * 用户登录名
         */
        private String loginName;
        /**
         * 登陆IP
         */
        private String ipAddr;
        /**
         * 部门编号
         */
        private String deptNo;

        @Override
        public String toString() {
            return "SearchForm{" +
                    "sessionId='" + sessionId + '\'' +
                    ", status='" + status + '\'' +
                    ", loginName='" + loginName + '\'' +
                    ", ipAddr='" + ipAddr + '\'' +
                    ", deptNo='" + deptNo + '\'' +
                    '}';
        }
    }

    private SearchForm getSearchForm() {
        SearchForm searchForm = (SearchForm) request.getSession().getAttribute(SEARCH_FORM_KEY);
        if (searchForm == null) {
            searchForm = new SearchForm();
        }
        setSearchForm(searchForm);
        return searchForm;
    }

    private void setSearchForm(SearchForm searchForm) {
        request.getSession().setAttribute(SEARCH_FORM_KEY, searchForm);
    }

    private void cleanSearchForm() {
        request.getSession().removeAttribute(SEARCH_FORM_KEY);
    }

}
