package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.domains.entity.domain.UserDomainEnvCreateValidator;
import com.adminpro.system.rbac.domains.entity.domain.UserDomainEnvEntity;
import com.adminpro.system.rbac.domains.entity.domain.UserDomainEnvService;
import com.adminpro.system.rbac.domains.entity.domain.UserDomainEnvUpdateValidator;
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

import java.util.List;

/**
 * 用户域环境配置管理控制器
 * <p>
 * 提供用户域环境配置的增删改查功能，包括环境配置列表查询、环境配置详情查询、环境配置创建、环境配置更新、环境配置删除等操作
 * </p>
 *
 * @author simon
 * @date 2020-06-14
 */
@Tag(name = "用户域环境配置管理", description = "用户域环境配置的增删改查接口")
@RestController
@RequestMapping(UserDomainEnvController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:user_domain_env')")
public class UserDomainEnvController extends BaseController {
    protected static final String PREFIX_URL = "/api/v1/domain-envs";
    protected static final String SEARCH_FORM_KEY = "userDomainEnvSearchForm";

    @Autowired
    private UserDomainEnvService userDomainEnvService;

    @Autowired
    private UserDomainEnvCreateValidator userDomainEnvCreateValidator;

    @Autowired
    private UserDomainEnvUpdateValidator userDomainEnvUpdateValidator;

    /**
     * 查询用户域环境配置列表
     * <p>
     * 根据查询条件获取用户域环境配置列表，支持按用户域进行过滤和分页查询
     * </p>
     *
     * @param searchForm 查询条件表单，包含用户域等过滤条件
     * @return 用户域环境配置查询结果集，包含数据和分页信息
     */
    @Operation(summary = "查询用户域环境配置列表", description = "根据查询条件获取用户域环境配置列表，支持按用户域进行过滤和分页查询")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDomainEnvEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping(value = "/list")
    public R<QueryResultSet<UserDomainEnvEntity>> list(
            @Parameter(description = "查询条件表单", required = true) @RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String userDomain = searchForm.getUserDomain();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(userDomain)) {
            param.addFilter("userDomain", userDomain);
        }

        QueryResultSet<UserDomainEnvEntity> resultSet = userDomainEnvService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 创建用户域环境配置
     * <p>
     * 新增一个用户域环境配置，包含登录URL、首页URL、错误页URL、布局等配置信息
     * </p>
     *
     * @param userDomainEnv 用户域环境配置实体信息
     * @return 操作结果
     */
    @Operation(summary = "创建用户域环境配置", description = "新增一个用户域环境配置，包含登录URL、首页URL、错误页URL、布局等配置信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping(value = "/create")
    public R create(
            @Parameter(description = "用户域环境配置实体信息", required = true) @RequestBody UserDomainEnvEntity userDomainEnv) {
        BeanUtil.beanAttributeValueTrim(userDomainEnv);
        MessageBundle messageBundle = getMessageBundle();
        userDomainEnvCreateValidator.validate(userDomainEnv, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String commonRole = userDomainEnv.getCommonRole();
            String description = userDomainEnv.getDescription();
            String errorPageUrl = userDomainEnv.getErrorPageUrl();
            String fatalErrorPageUrl = userDomainEnv.getFatalErrorPageUrl();
            String homePageUrl = userDomainEnv.getHomePageUrl();
            String loginUrl = userDomainEnv.getLoginUrl();
            String layout = userDomainEnv.getLayout();
            String sessionExpiredUrl = userDomainEnv.getSessionExpiredUrl();
            String userDomain = userDomainEnv.getUserDomain();

            UserDomainEnvEntity entity = new UserDomainEnvEntity();
            entity.setCommonRole(commonRole);
            entity.setDescription(description);
            entity.setErrorPageUrl(errorPageUrl);
            entity.setFatalErrorPageUrl(fatalErrorPageUrl);
            entity.setHomePageUrl(homePageUrl);
            entity.setLoginUrl(loginUrl);
            entity.setLayout(layout);
            entity.setSessionExpiredUrl(sessionExpiredUrl);
            entity.setUserDomain(userDomain);

            userDomainEnvService.create(entity);
            return R.ok();
        }
    }

    /**
     * 更新用户域环境配置
     * <p>
     * 更新已有用户域环境配置的信息
     * </p>
     *
     * @param userDomainEnv 用户域环境配置实体信息
     * @return 操作结果
     */
    @Operation(summary = "更新用户域环境配置", description = "更新已有用户域环境配置的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "用户域环境配置不存在")
    })
    @PutMapping(value = "/{id}")
    public R editSave(@PathVariable String id,
            @Parameter(description = "用户域环境配置实体信息", required = true) @RequestBody UserDomainEnvEntity userDomainEnv) {
        userDomainEnv.setId(id);
        BeanUtil.beanAttributeValueTrim(userDomainEnv);
        MessageBundle messageBundle = getMessageBundle();

        userDomainEnvUpdateValidator.validate(userDomainEnv, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            UserDomainEnvEntity entity = userDomainEnvService.findById(userDomainEnv.getId());
            String commonRole = userDomainEnv.getCommonRole();
            String description = userDomainEnv.getDescription();
            String errorPageUrl = userDomainEnv.getErrorPageUrl();
            String fatalErrorPageUrl = userDomainEnv.getFatalErrorPageUrl();
            String homePageUrl = userDomainEnv.getHomePageUrl();
            String layout = userDomainEnv.getLayout();
            String sessionExpiredUrl = userDomainEnv.getSessionExpiredUrl();
            String userDomain = userDomainEnv.getUserDomain();
            String loginUrl = userDomainEnv.getLoginUrl();

            entity.setCommonRole(commonRole);
            entity.setDescription(description);
            entity.setErrorPageUrl(errorPageUrl);
            entity.setFatalErrorPageUrl(fatalErrorPageUrl);
            entity.setHomePageUrl(homePageUrl);
            entity.setLoginUrl(loginUrl);
            entity.setLayout(layout);
            entity.setSessionExpiredUrl(sessionExpiredUrl);
            entity.setUserDomain(userDomain);

            userDomainEnvService.update(entity);
            return R.ok();
        }
    }

    /**
     * 查询用户域环境配置详情
     * <p>
     * 根据用户域环境配置ID获取配置的详细信息
     * </p>
     *
     * @param id 用户域环境配置ID
     * @return 用户域环境配置详细信息
     */
    @Operation(summary = "查询用户域环境配置详情", description = "根据用户域环境配置ID获取配置的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDomainEnvEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "用户域环境配置不存在")
    })
    @GetMapping(value = "/{id}")
    public R<UserDomainEnvEntity> detail(
            @Parameter(description = "用户域环境配置ID", required = true) @PathVariable String id) {
        UserDomainEnvEntity entity = userDomainEnvService.findById(id);
        if (entity != null) {
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 批量删除用户域环境配置
     * <p>
     * 根据多个配置ID批量删除用户域环境配置，ID之间用逗号分隔
     * </p>
     *
     * @param ids 用户域环境配置ID列表，多个ID用逗号分隔
     * @return 操作结果
     */
    @Operation(summary = "批量删除用户域环境配置", description = "根据多个配置ID批量删除用户域环境配置，ID之间用逗号分隔")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @DeleteMapping
    public R remove(
            @Parameter(description = "用户域环境配置ID列表，多个ID用逗号分隔", required = true, example = "1,2,3") @RequestParam("ids") String ids) {
        List<String> idList = BatchOperationValidator.validateAndParseIds(ids);
        userDomainEnvService.deleteByIds(String.join(",", idList));
        return R.ok();
    }

    @Data
    public static class SearchForm extends BaseSearchForm {
        private String userDomain;

        @Override
        public String toString() {
            return "SearchForm{" +
                    "userDomain='" + userDomain + '\'' +
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
