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
import com.adminpro.system.rbac.domains.entity.post.PostCreateValidator;
import com.adminpro.system.rbac.domains.entity.post.PostEntity;
import com.adminpro.system.rbac.domains.entity.post.PostService;
import com.adminpro.system.rbac.domains.entity.post.PostUpdateValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 职位管理控制器
 * <p>
 * 提供职位的增删改查功能，包括职位列表查询、职位详情查询、职位创建、职位更新、职位删除等操作
 * </p>
 *
 * @author simon
 * @date 2020-05-21
 */
@Tag(name = "职位管理", description = "职位的增删改查接口")
@RestController
@RequestMapping(PostController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:post')")
public class PostController extends BaseController {
    protected static final String PREFIX_URL = "/api/v1/posts";
    protected static final String SEARCH_FORM_KEY = "postSearchForm";

    @Autowired
    private PostService postService;

    @Autowired
    private PostCreateValidator postCreateValidator;

    @Autowired
    private PostUpdateValidator postUpdateValidator;

    /**
     * 查询职位列表
     * <p>
     * 根据查询条件获取职位列表，支持按职位编码、名称、状态等条件进行过滤和分页查询
     * </p>
     *
     * @param searchForm 查询条件表单，包含职位编码、名称、状态等过滤条件
     * @return 职位查询结果集，包含数据和分页信息
     */
    @Operation(summary = "查询职位列表", description = "根据查询条件获取职位列表，支持按职位编码、名称、状态等条件进行过滤和分页查询")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping
    public R<QueryResultSet<PostEntity>> list(
            @Parameter(description = "查询条件表单", required = true) @RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String code = searchForm.getCode();
        String name = searchForm.getName();
        String status = searchForm.getStatus();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(code)) {
            param.addFilter("code", code);
        }
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", status);
        }
        QueryResultSet<PostEntity> resultSet = postService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 查询职位详情
     * <p>
     * 根据职位ID获取职位的详细信息
     * </p>
     *
     * @param id 职位ID
     * @return 职位详细信息
     */
    @Operation(summary = "查询职位详情", description = "根据职位ID获取职位的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "职位不存在")
    })
    @GetMapping(value = "/{id}")
    public R<PostEntity> detail(@Parameter(description = "职位ID", required = true) @PathVariable String id) {
        PostEntity entity = postService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 创建职位
     * <p>
     * 新增一个职位，包含职位编码、名称、排序、状态等信息
     * </p>
     *
     * @param post 职位实体信息
     * @return 操作结果
     */
    @Operation(summary = "创建职位", description = "新增一个职位，包含职位编码、名称、排序、状态等信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @SysLog("创建职位")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public R create(@Parameter(description = "职位实体信息", required = true) @RequestBody PostEntity post) {
        BeanUtil.beanAttributeValueTrim(post);
        MessageBundle messageBundle = getMessageBundle();
        postCreateValidator.validate(post, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String code = post.getCode();
            String name = post.getName();
            Integer sort = post.getSort();
            String status = post.getStatus();
            String remark = post.getRemark();

            PostEntity entity = new PostEntity();
            entity.setCode(code);
            entity.setName(name);
            entity.setSort(sort);
            entity.setStatus(status);
            entity.setRemark(remark);

            postService.create(entity);
            return R.ok();
        }
    }

    /**
     * 更新职位
     * <p>
     * 更新已有职位的信息
     * </p>
     *
     * @param post 职位实体信息
     * @return 操作结果
     */
    @Operation(summary = "更新职位", description = "更新已有职位的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "职位不存在")
    })
    @SysLog("更新职位")
    @PutMapping(value = "/{id}")
    public R editSave(@Parameter(description = "职位实体信息", required = true) @RequestBody PostEntity post) {
        BeanUtil.beanAttributeValueTrim(post);
        MessageBundle messageBundle = getMessageBundle();

        postUpdateValidator.validate(post, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            PostEntity entity = postService.findById(post.getId());
            String code = post.getCode();
            String name = post.getName();
            Integer sort = post.getSort();
            String status = post.getStatus();
            String remark = post.getRemark();

            entity.setCode(code);
            entity.setName(name);
            entity.setSort(sort);
            entity.setStatus(status);
            entity.setRemark(remark);

            postService.update(entity);
            return R.ok();
        }
    }

    /**
     * 批量删除职位
     * <p>
     * 根据多个职位ID批量删除职位，ID之间用逗号分隔
     * </p>
     *
     * @param ids 职位ID列表，多个ID用逗号分隔
     * @return 操作结果
     */
    @Operation(summary = "批量删除职位", description = "根据多个职位ID批量删除职位，ID之间用逗号分隔")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @SysLog("删除职位")
    @DeleteMapping
    public R remove(
            @Parameter(description = "职位ID列表，多个ID用逗号分隔", required = true, example = "1,2,3") @RequestParam("ids") String ids) {
        // 使用验证工具类解析和验证参数
        List<String> postIdList = BatchOperationValidator.validateAndParseIds(ids);
        postService.deleteByIds(StringUtils.join(postIdList, ","));
        return R.ok();
    }

    public static class SearchForm extends BaseSearchForm {
        private String code;
        private String name;
        private String status;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "SearchForm{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", status='" + status + '\'' +
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
