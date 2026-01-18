package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.tools.domains.entity.config.ConfigCreateValidator;
import com.adminpro.system.tools.domains.entity.config.ConfigEntity;
import com.adminpro.system.tools.domains.entity.config.ConfigService;
import com.adminpro.system.tools.domains.entity.config.ConfigUpdateValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参数配置管理控制器
 * <p>
 * 提供系统参数配置的增删改查功能，包括：
 * <ul>
 * <li>参数配置列表查询（支持分页和多条件筛选）</li>
 * <li>参数配置详情查看</li>
 * <li>参数配置的新增、修改、删除</li>
 * </ul>
 * </p>
 *
 * @author simon
 * @date 2020-06-15
 */
@Tag(name = "参数配置管理", description = "系统参数配置管理接口，提供参数配置的增删改查功能")
@RestController
@RequestMapping(ConfigController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:config')")
/**
 * 使用 Lombok @RequiredArgsConstructor 自动生成构造器进行依赖注入。
 * 所有 final 字段将通过构造器自动注入，无需显式编写 @Autowired。
 * 添加新依赖时，只需添加 private final 字段即可。
 */
@RequiredArgsConstructor
public class ConfigController extends BaseController {

    protected static final String PREFIX_URL = "/api/v1/tools/config";
    protected static final String SEARCH_FORM_KEY = "configSearchForm";

    private final ConfigService configService;
    private final ConfigCreateValidator configCreateValidator;
    private final ConfigUpdateValidator configUpdateValidator;

    /**
     * 查询参数配置列表
     * <p>
     * 支持按参数名称、参数键、参数值等条件进行分页查询
     * </p>
     *
     * @param searchForm 搜索表单，包含分页信息和筛选条件
     * @return 包含参数配置列表和分页信息的查询结果集
     */
    @Operation(summary = "查询参数配置列表", description = "支持按参数名称、参数键、参数值等条件进行分页查询")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "搜索条件", required = true, content = @Content(schema = @Schema(implementation = SearchForm.class)))
    @ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 QueryResultSet<ConfigEntity> 列表
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @PostMapping("/search")
    public R<QueryResultSet<ConfigEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String name = searchForm.getName();
        String key = searchForm.getKey();
        String value = searchForm.getValue();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(key)) {
            param.addFilter("key", key);
        }
        if (StringUtils.isNotEmpty(value)) {
            param.addFilter("value", value);
        }

        QueryResultSet<ConfigEntity> resultSet = configService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 新增保存参数配置
     * <p>
     * 创建新的系统参数配置，包含参数名称、参数键、参数值、系统标识及备注
     * </p>
     *
     * @param config 配置实体对象，包含参数配置的信息
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "新增参数配置", description = "创建新的系统参数配置，包含参数名称、参数键、参数值、系统标识及备注")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "配置实体对象", required = true, content = @Content(schema = @Schema(implementation = ConfigEntity.class)))
    @ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 创建成功
                - restCode=400: 请求参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @SysLog("创建配置")
    @PostMapping
    public R create(@RequestBody ConfigEntity config) {
        BeanUtil.beanAttributeValueTrim(config);
        MessageBundle messageBundle = getMessageBundle();
        configCreateValidator.validate(config, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String key = config.getKey();
            String name = config.getName();
            String value = config.getValue();
            Integer system = config.getSystem();
            String remark = config.getRemark();

            ConfigEntity entity = new ConfigEntity();
            entity.setKey(key);
            entity.setName(name);
            entity.setValue(value);
            entity.setSystem(system);
            entity.setRemark(remark);

            configService.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改保存参数配置
     * <p>
     * 根据配置ID更新参数配置信息，包含参数名称、参数键、参数值、系统标识及备注
     * </p>
     *
     * @param config 配置实体对象，必须包含ID字段
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "修改参数配置", description = "根据配置ID更新参数配置信息，包含参数名称、参数键、参数值、系统标识及备注")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "配置实体对象，必须包含ID字段", required = true, content = @Content(schema = @Schema(implementation = ConfigEntity.class)))
    @ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 更新成功
                - restCode=400: 请求参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=404: 配置不存在
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @SysLog("更新配置")
    @PutMapping(value = "/{id}")
    public R editSave(@RequestBody ConfigEntity config) {
        BeanUtil.beanAttributeValueTrim(config);
        MessageBundle messageBundle = getMessageBundle();

        configUpdateValidator.validate(config, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            ConfigEntity entity = configService.findById(config.getId());
            String key = config.getKey();
            String name = config.getName();
            String value = config.getValue();
            Integer system = config.getSystem();
            String remark = config.getRemark();

            entity.setKey(key);
            entity.setName(name);
            entity.setValue(value);
            entity.setSystem(system);
            entity.setRemark(remark);

            configService.update(entity);
            return R.ok();
        }
    }

    /**
     * 获取详细信息
     * <p>
     * 根据配置ID查询参数配置的详细信息
     * </p>
     *
     * @param id 配置ID
     * @return 配置实体对象，不包含审计时间字段
     */
    @Operation(summary = "获取配置详情", description = "根据配置ID查询参数配置的详细信息")
    @Parameter(name = "id", description = "配置ID", required = true, schema = @Schema(type = "string"))
    @ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 ConfigEntity 对象
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=404: 配置不存在
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @GetMapping(value = "/{id}")
    public R<ConfigEntity> detail(@PathVariable String id) {
        ConfigEntity entity = configService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 删除参数配置
     * <p>
     * 支持批量删除参数配置，多个ID用逗号分隔
     * </p>
     *
     * @param ids 配置ID列表，多个ID用逗号分隔
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "删除参数配置", description = "支持批量删除参数配置，多个ID用逗号分隔")
    @Parameter(name = "ids", description = "配置ID列表，多个ID用逗号分隔", required = true, schema = @Schema(type = "string"))
    @ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 删除成功
                - restCode=400: 请求参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @SysLog("删除配置")
    @DeleteMapping
    public R remove(@RequestParam("ids") String ids) {
        // 使用验证工具类解析和验证参数
        List<String> configIdList = BatchOperationValidator.validateAndParseIds(ids);
        configService.deleteByIds(StringUtils.join(configIdList, ","));
        return R.ok();
    }

    @Data
    @lombok.EqualsAndHashCode(callSuper = false)
    public static class SearchForm extends BaseSearchForm {
        private String name;
        private String key;
        private String value;

        @Override
        public String toString() {
            return "SearchForm{" +
                    "name='" + name + '\'' +
                    ", key='" + key + '\'' +
                    ", value='" + value + '\'' +
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
