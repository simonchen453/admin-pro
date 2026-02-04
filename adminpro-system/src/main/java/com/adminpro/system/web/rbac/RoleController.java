package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.domains.entity.role.RoleCreateValidator;
import com.adminpro.system.rbac.domains.entity.role.RoleEntity;
import com.adminpro.system.rbac.domains.entity.role.RoleService;
import com.adminpro.system.rbac.domains.entity.role.RoleUpdateValidator;
import com.adminpro.system.rbac.domains.vo.role.ListRoleVo;
import com.adminpro.system.rbac.domains.vo.role.ListRoleVoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色 信息操作处理
 *
 * @author simon
 * @date 2020-06-08
 */
@Tag(name = "角色管理", description = "角色的增删改查接口")
@RestController
@RequestMapping(RoleController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:role')")
/**
 * 使用 Lombok @RequiredArgsConstructor 自动生成构造器进行依赖注入。
 * 所有 final 字段将通过构造器自动注入，无需显式编写 @Autowired。
 * 添加新依赖时，只需添加 private final 字段即可。
 */
@RequiredArgsConstructor
public class RoleController extends BaseController {
    protected static final String PREFIX_URL = "/api/v1/roles";
    protected static final String SEARCH_FORM_KEY = "roleSearchForm";

    private final RoleService roleService;
    private final RoleCreateValidator roleCreateValidator;
    private final RoleUpdateValidator roleUpdateValidator;

    /**
     * 查询角色列表
     */
    @Operation(summary = "查询角色列表", description = "根据查询条件分页查询角色列表，支持按名称、显示名称、状态、系统角色等条件筛选")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 QueryResultSet<ListRoleVo> 列表
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @PostMapping(value = "/search")
    public R<QueryResultSet<ListRoleVo>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String name = searchForm.getName();
        String display = searchForm.getDisplay();
        String status = searchForm.getStatus();
        Boolean system = searchForm.system;

        SearchParam param = startPaging(searchForm);
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(display)) {
            param.addFilter("display", display);
        }
        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", status);
        }
        if (system != null) {
            param.addFilter("system", system);
        }
        QueryResultSet<ListRoleVo> resultSet = roleService.search(param).map(ListRoleVoConverter.class);
        return R.ok(resultSet);
    }

    /**
     * 新增保存角色
     */
    @SysLog("创建角色")
    @Operation(summary = "创建角色", description = "新增保存角色，包括角色名称、显示名称、状态、菜单权限等")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 创建成功
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @PostMapping
    public R create(@RequestBody RoleEntity role) {
        BeanUtil.beanAttributeValueTrim(role);
        MessageBundle messageBundle = getMessageBundle();
        roleCreateValidator.validate(role, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String name = role.getName();
            String display = role.getDisplay();
            Boolean isSystem = role.isSystem();

            RoleEntity entity = new RoleEntity();
            entity.setName(name);
            entity.setDisplay(display);
            entity.setSystem(isSystem);
            entity.setStatus(role.getStatus());
            entity.setSystem(role.isSystem());
            entity.setMenuIds(role.getMenuIds());
            roleService.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改保存角色
     */
    @SysLog("更新角色")
    @Operation(summary = "更新角色", description = "修改保存角色信息，包括角色名称、显示名称、状态、菜单权限等")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 更新成功
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=404: 角色不存在
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @PutMapping(value = "/{id}")
    public R editSave(@RequestBody RoleEntity role) {
        BeanUtil.beanAttributeValueTrim(role);
        MessageBundle messageBundle = getMessageBundle();

        roleUpdateValidator.validate(role, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            RoleEntity entity = roleService.findById(role.getId());
            String name = role.getName();
            String display = role.getDisplay();
            Boolean isSystem = role.isSystem();

            entity.setName(name);
            entity.setDisplay(display);
            entity.setSystem(isSystem);
            entity.setStatus(role.getStatus());
            entity.setMenuIds(role.getMenuIds());
            roleService.update(entity);
            return R.ok();
        }
    }

    /**
     * 获取详细信息
     *
     * @param id
     * @return
     */
    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色的详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 RoleEntity 对象
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=404: 角色不存在
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @GetMapping(value = "/{id}")
    public R<RoleEntity> detail(@PathVariable String id) {
        RoleEntity entity = roleService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 删除角色
     */
    @SysLog("删除角色")
    @Operation(summary = "删除角色", description = "批量删除角色，支持一次删除多个角色")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 批量删除成功
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @DeleteMapping
    public R remove(@RequestParam("ids") String ids) {
        // 使用验证工具类解析和验证参数
        List<String> roleIdList = BatchOperationValidator.validateAndParseIds(ids);
        roleService.deleteByIds(StringUtils.join(roleIdList, ","));
        return R.ok();
    }

    @Data
    @lombok.EqualsAndHashCode(callSuper = false)
    public static class SearchForm extends BaseSearchForm {
        private String name;
        private String display;
        private String status;
        private Boolean system;
    }

}
