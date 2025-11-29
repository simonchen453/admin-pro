package com.adminpro.web.rbac;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.annotation.SysLog;
import com.adminpro.rbac.domains.entity.post.PostCreateValidator;
import com.adminpro.rbac.domains.entity.post.PostEntity;
import com.adminpro.rbac.domains.entity.post.PostService;
import com.adminpro.rbac.domains.entity.post.PostUpdateValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 职位 信息操作处理
 *
 * @author simon
 * @date 2020-05-21
 */
@RestController
@RequestMapping("/admin/post")
@PreAuthorize("@ss.hasPermission('system:post')")
public class PostController extends BaseRoutingController {
    protected static final String PREFIX = "admin/post";
    protected static final String PREFIX_URL = "/admin/post";
    protected static final String SEARCH_FORM_KEY = "postSearchForm";

    @Autowired
    private PostService postService;

    @Autowired
    private PostCreateValidator postCreateValidator;

    @Autowired
    private PostUpdateValidator postUpdateValidator;

    /**
     * 查询职位列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<QueryResultSet<PostEntity>> list(@RequestBody SearchForm searchForm) {
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
     * 获取详细信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public R<PostEntity> detail(@PathVariable String id) {
        PostEntity entity = postService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    /**
     * 新增保存职位
     */
    @SysLog("创建职位")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public R create(@RequestBody PostEntity post) {
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
     * 修改保存职位
     */
    @SysLog("更新职位")
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    public R editSave(@RequestBody PostEntity post) {
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
     * 删除职位
     */
    @SysLog("删除职位")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public R remove(@RequestParam("ids") String ids) {
        postService.deleteByIds(ids);
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
