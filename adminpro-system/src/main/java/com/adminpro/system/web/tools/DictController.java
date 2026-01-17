package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.enums.CommonStatus;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.tools.domains.entity.dict.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
 * 字典类型管理控制器
 * <p>
 * 提供字典类型的增删改查功能，包括：
 * <ul>
 * <li>字典类型列表查询（支持分页和多条件筛选）</li>
 * <li>字典类型详情查看</li>
 * <li>字典类型的新增、修改、删除</li>
 * <li>字典类型的激活和停用操作</li>
 * </ul>
 * </p>
 *
 * @author simon
 * @date 2020-08-22
 */
@Tag(name = "字典管理", description = "字典类型管理接口，提供字典的增删改查及状态管理功能")
@RestController
@RequestMapping(DictController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:dict')")
public class DictController extends BaseController {

    protected static final String PREFIX_URL = "/api/v1/tools/dict";
    protected static final String SEARCH_FORM_KEY = "dictSearchForm";

    @Autowired
    private DictService dictService;

    @Autowired
    private DictCreateValidator dictCreateValidator;

    @Autowired
    private DictUpdateValidator dictUpdateValidator;

    /**
     * 查询字典类型列表
     * <p>
     * 支持按字典名称、字典类型、状态等条件进行分页查询
     * </p>
     *
     * @param searchForm 搜索表单，包含分页信息和筛选条件
     * @return 包含字典类型列表和分页信息的查询结果集
     */
    @Operation(summary = "查询字典类型列表", description = "支持按字典名称、字典类型、状态等条件进行分页查询")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "搜索条件", required = true, content = @Content(schema = @Schema(implementation = SearchForm.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = R.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping("/search")
    public R<QueryResultSet<DictEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String name = searchForm.getName();
        String key = searchForm.getKey();
        String status = searchForm.getStatus();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(key)) {
            param.addFilter("key", key);
        }
        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", status);
        }
        QueryResultSet<DictEntity> resultSet = dictService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 新增保存字典类型
     * <p>
     * 创建新的字典类型，包含字典名称、类型键、状态、备注及字典数据
     * </p>
     *
     * @param dict 字典实体对象，包含字典类型的信息
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "新增字典类型", description = "创建新的字典类型，包含字典名称、类型键、状态、备注及字典数据")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "字典实体对象", required = true, content = @Content(schema = @Schema(implementation = DictEntity.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @SysLog("创建字典")
    @PostMapping
    public R create(@RequestBody DictEntity dict) {
        BeanUtil.beanAttributeValueTrim(dict);
        MessageBundle messageBundle = getMessageBundle();
        dictCreateValidator.validate(dict, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String name = dict.getName();
            String key = dict.getKey();
            String status = dict.getStatus();
            String remark = dict.getRemark();

            DictEntity entity = new DictEntity();
            entity.setName(name);
            entity.setKey(key);
            entity.setStatus(status);
            entity.setRemark(remark);
            entity.setData(dict.getData());

            dictService.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改保存字典类型
     * <p>
     * 根据字典ID更新字典类型信息，包含字典名称、类型键、状态、备注及字典数据
     * </p>
     *
     * @param dict 字典实体对象，必须包含ID字段
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "修改字典类型", description = "根据字典ID更新字典类型信息，包含字典名称、类型键、状态、备注及字典数据")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "字典实体对象，必须包含ID字段", required = true, content = @Content(schema = @Schema(implementation = DictEntity.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "字典不存在")
    })
    @SysLog("更新字典")
    @PutMapping(value = "/{id}")
    public R editSave(@RequestBody DictEntity dict) {
        BeanUtil.beanAttributeValueTrim(dict);
        MessageBundle messageBundle = getMessageBundle();

        dictUpdateValidator.validate(dict, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            DictEntity entity = dictService.findById(dict.getId());
            String name = dict.getName();
            String key = dict.getKey();
            String status = dict.getStatus();
            String remark = dict.getRemark();

            entity.setName(name);
            entity.setKey(key);
            entity.setStatus(status);
            entity.setRemark(remark);
            entity.setData(dict.getData());

            dictService.update(entity);
            return R.ok();
        }
    }

    /**
     * 获取详细信息
     * <p>
     * 根据字典ID查询字典类型的详细信息
     * </p>
     *
     * @param id 字典ID
     * @return 字典实体对象，不包含审计时间字段
     */
    @Operation(summary = "获取字典详情", description = "根据字典ID查询字典类型的详细信息")
    @Parameter(name = "id", description = "字典ID", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = DictEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "字典不存在")
    })
    @GetMapping(value = "/{id}")
    public R<DictEntity> detail(@PathVariable String id) {
        DictEntity entity = dictService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 删除字典类型
     * <p>
     * 支持批量删除字典类型，多个ID用逗号分隔
     * </p>
     *
     * @param ids 字典ID列表，多个ID用逗号分隔
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "删除字典类型", description = "支持批量删除字典类型，多个ID用逗号分隔")
    @Parameter(name = "ids", description = "字典ID列表，多个ID用逗号分隔", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @SysLog("删除字典")
    @DeleteMapping
    public R remove(@RequestParam("ids") String ids) {
        // 使用验证工具类解析和验证参数
        List<String> dictIdList = BatchOperationValidator.validateAndParseIds(ids);
        dictService.deleteByIds(StringUtils.join(dictIdList, ","));
        return R.ok();
    }

    /**
     * 激活字典类型
     * <p>
     * 将指定字典类型的状态设置为激活状态
     * </p>
     *
     * @param id 字典ID
     * @return 更新后的字典实体对象
     */
    @Operation(summary = "激活字典类型", description = "将指定字典类型的状态设置为激活状态")
    @Parameter(name = "id", description = "字典ID", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "激活成功", content = @Content(schema = @Schema(implementation = DictEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "字典不存在")
    })
    @SysLog("激活字典")
    @PatchMapping("/{id}/active")
    public R<DictEntity> active(@PathVariable String id) {
        DictEntity entity = dictService.findById(id);
        if (entity != null) {
            entity.setStatus(CommonStatus.ACTIVE.getCode());
            dictService.update(entity);
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 停用字典类型
     * <p>
     * 将指定字典类型的状态设置为停用状态
     * </p>
     *
     * @param id 字典ID
     * @return 更新后的字典实体对象
     */
    @Operation(summary = "停用字典类型", description = "将指定字典类型的状态设置为停用状态")
    @Parameter(name = "id", description = "字典ID", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "停用成功", content = @Content(schema = @Schema(implementation = DictEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "字典不存在")
    })
    @SysLog("停用字典")
    @PatchMapping("/{id}/inactive")
    public R<DictEntity> inactive(@PathVariable String id) {
        DictEntity entity = dictService.findById(id);
        if (entity != null) {
            entity.setStatus(CommonStatus.INACTIVE.getCode());
            dictService.update(entity);
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    @Data
    @lombok.EqualsAndHashCode(callSuper = false)
    public static class SearchForm extends BaseSearchForm {
        /**
         * 字典名称
         */
        private String name;
        /**
         * 字典类型
         */
        private String key;
        /**
         * 状态
         */
        private String status;
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
