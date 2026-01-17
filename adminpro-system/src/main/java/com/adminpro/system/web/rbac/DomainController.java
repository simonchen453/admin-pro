package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.domains.entity.domain.DomainCreateValidator;
import com.adminpro.system.rbac.domains.entity.domain.DomainEntity;
import com.adminpro.system.rbac.domains.entity.domain.DomainService;
import com.adminpro.system.rbac.domains.entity.domain.DomainUpdateValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户域管理控制器
 * <p>
 * 提供用户域的增删改查功能，包括用户域列表查询、用户域详情查询、用户域创建、用户域更新等操作
 * </p>
 *
 * @author simon
 * @date 2020-06-14
 */
@Tag(name = "用户域管理", description = "用户域的增删改查接口")
@RestController
@RequestMapping(DomainController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:domain')")
/**
 * 使用 Lombok @RequiredArgsConstructor 自动生成构造器进行依赖注入。
 * 所有 final 字段将通过构造器自动注入，无需显式编写 @Autowired。
 * 添加新依赖时，只需添加 private final 字段即可。
 */
@RequiredArgsConstructor
public class DomainController extends BaseController {
    protected static final String PREFIX_URL = "/api/v1/domains";
    protected static final String SEARCH_FORM_KEY = "domainSearchForm";

    private final DomainService domainService;
    private final DomainCreateValidator domainCreateValidator;
    private final DomainUpdateValidator domainUpdateValidator;

    /**
     * 查询用户域列表
     * <p>
     * 根据查询条件获取用户域列表，支持按域名称、显示名称等条件进行过滤和分页查询
     * </p>
     *
     * @param searchForm 查询条件表单，包含域名称、显示名称等过滤条件
     * @return 用户域查询结果集，包含数据和分页信息
     */
    @Operation(summary = "查询用户域列表", description = "根据查询条件获取用户域列表，支持按域名称、显示名称等条件进行过滤和分页查询")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DomainEntity.class))),
            @ApiResponse(responseCode = "200", description = "未授权（restCode=401）"),
            @ApiResponse(responseCode = "200", description = "无权限访问（restCode=403）")
    })
    @SysLog("查询用户域列表")
    @PostMapping(value = "/search")
    public R<QueryResultSet<DomainEntity>> list(
            @Parameter(description = "查询条件表单", required = true) @RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String name = searchForm.getName();
        String display = searchForm.getDisplay();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(display)) {
            param.addFilter("display", display);
        }
        QueryResultSet<DomainEntity> resultSet = domainService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 获取所有用户域列表（简化版）
     * <p>
     * 获取所有用户域的简化列表，用于下拉选择等场景
     * </p>
     *
     * @return 用户域列表
     */
    @Operation(summary = "获取所有用户域", description = "获取所有用户域的简化列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "200", description = "未授权（restCode=401）")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public R<java.util.List<DomainEntity>> getAllDomains() {
        SearchParam param = new SearchParam();
        java.util.List<DomainEntity> domains = domainService.findAll();
        return R.ok(domains);
    }

    /**
     * 创建用户域
     * <p>
     * 新增一个用户域，包含域名称、显示名称、是否系统域等信息
     * </p>
     *
     * @param userDomain 用户域实体信息
     * @return 操作结果
     */
    @Operation(summary = "创建用户域", description = "新增一个用户域，包含域名称、显示名称、是否系统域等信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "200", description = "请求参数错误（restCode=400）"),
            @ApiResponse(responseCode = "200", description = "未授权（restCode=401）"),
            @ApiResponse(responseCode = "200", description = "无权限访问（restCode=403）")
    })
    @SysLog("创建用户域")
    @PostMapping(value = "/create")
    public R create(@Parameter(description = "用户域实体信息", required = true) @RequestBody DomainEntity userDomain) {
        BeanUtil.beanAttributeValueTrim(userDomain);
        MessageBundle messageBundle = getMessageBundle();
        domainCreateValidator.validate(userDomain, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String name = userDomain.getName();
            String display = userDomain.getDisplay();
            Boolean isSystem = userDomain.getIsSystem();

            DomainEntity entity = new DomainEntity();
            entity.setName(name);
            entity.setDisplay(display);
            entity.setIsSystem(isSystem);

            domainService.create(entity);
            return R.ok();
        }
    }

    /**
     * 更新用户域
     * <p>
     * 更新已有用户域的信息
     * </p>
     *
     * @param userDomain 用户域实体信息
     * @return 操作结果
     */
    @Operation(summary = "更新用户域", description = "更新已有用户域的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "200", description = "请求参数错误（restCode=400）"),
            @ApiResponse(responseCode = "200", description = "未授权（restCode=401）"),
            @ApiResponse(responseCode = "200", description = "无权限访问（restCode=403）"),
            @ApiResponse(responseCode = "200", description = "用户域不存在（restCode=404）")
    })
    @SysLog("更新用户域")
    @PutMapping(value = "/{id}")
    public R editSave(@PathVariable String id,
            @Parameter(description = "用户域实体信息", required = true) @RequestBody DomainEntity userDomain) {
        userDomain.setId(id);
        BeanUtil.beanAttributeValueTrim(userDomain);
        MessageBundle messageBundle = getMessageBundle();

        domainUpdateValidator.validate(userDomain, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            DomainEntity entity = domainService.findById(userDomain.getId());
            String name = userDomain.getName();
            String display = userDomain.getDisplay();
            Boolean isSystem = userDomain.getIsSystem();

            entity.setName(name);
            entity.setDisplay(display);
            entity.setIsSystem(isSystem);

            domainService.update(entity);
            return R.ok();
        }
    }

    /**
     * 查询用户域详情
     * <p>
     * 根据用户域ID获取用户域的详细信息
     * </p>
     *
     * @param id 用户域ID
     * @return 用户域详细信息
     */
    @Operation(summary = "查询用户域详情", description = "根据用户域ID获取用户域的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DomainEntity.class))),
            @ApiResponse(responseCode = "200", description = "未授权（restCode=401）"),
            @ApiResponse(responseCode = "200", description = "无权限访问（restCode=403）"),
            @ApiResponse(responseCode = "200", description = "用户域不存在（restCode=404）")
    })
    @GetMapping(value = "/{id}")
    public R<DomainEntity> detail(@Parameter(description = "用户域ID", required = true) @PathVariable String id) {
        DomainEntity entity = domainService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 删除用户域
     * <p>
     * 根据用户域ID删除用户域
     * </p>
     *
     * @param id 用户域ID
     * @return 操作结果
     */
    @Operation(summary = "删除用户域", description = "根据用户域ID删除用户域")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "200", description = "未授权（restCode=401）"),
            @ApiResponse(responseCode = "200", description = "无权限访问（restCode=403）"),
            @ApiResponse(responseCode = "200", description = "用户域不存在（restCode=404）")
    })
    @SysLog("删除用户域")
    @DeleteMapping(value = "/{id}")
    public R delete(@Parameter(description = "用户域ID", required = true) @PathVariable String id) {
        DomainEntity entity = domainService.findById(id);
        if (entity != null) {
            domainService.delete(entity);
            return R.ok();
        } else {
            return R.error("用户域不存在");
        }
    }

    public static class SearchForm extends BaseSearchForm {
        private String name;
        private String display;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        @Override
        public String toString() {
            return "SearchForm{" +
                    "name='" + name + '\'' +
                    ", display='" + display + '\'' +
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
