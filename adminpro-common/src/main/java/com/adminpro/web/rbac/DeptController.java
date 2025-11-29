package com.adminpro.web.rbac;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.annotation.SysLog;
import com.adminpro.framework.common.helper.UploadDownloadHelper;
import com.adminpro.rbac.domains.entity.dept.DeptCreateValidator;
import com.adminpro.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.rbac.domains.entity.dept.DeptService;
import com.adminpro.rbac.domains.entity.dept.DeptUpdateValidator;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * 部门 信息操作处理
 *
 * @author simon
 * @date 2020-05-24
 */
@RestController
@RequestMapping("/admin/dept")
@PreAuthorize("@ss.hasPermission('system:dept')")
public class DeptController extends BaseRoutingController {
    protected static final String PREFIX = "admin/dept";
    protected static final String PREFIX_URL = "/admin/dept";
    protected static final String SEARCH_FORM_KEY = "deptSearchForm";

    @Autowired
    private UploadDownloadHelper uploadDownloadHelper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private DeptCreateValidator deptCreateValidator;

    @Autowired
    private DeptUpdateValidator deptUpdateValidator;

    /**
     * 查询部门列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<List<DeptEntity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String status = searchForm.getStatus();
        String name = searchForm.getName();
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        if (StringUtils.isNotEmpty(name)) {
            param.addFilter("name", name);
        }
        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", status);
        }

        List<DeptEntity> list = deptService.findByParam(param);
        return R.ok(list);
    }

    /**
     * 新增保存部门
     */
    @SysLog("创建部门")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public R create(@RequestBody DeptEntity dept) {
        MessageBundle messageBundle = getMessageBundle();
        BeanUtil.beanAttributeValueTrim(dept);
        deptCreateValidator.validate(dept, messageBundle);
        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            String no = dept.getNo();
            String parentId = dept.getParentId();
            String name = dept.getName();
            Integer orderNum = dept.getOrderNum();
            Integer delFlag = dept.getDelFlag();
            String description = dept.getDescription();
            String linkman = dept.getLinkman();
            String contact = dept.getContact();
            String phone = dept.getPhone();
            String email = dept.getEmail();
            String status = dept.getStatus();

            DeptEntity entity = new DeptEntity();
            entity.setNo(no);
            entity.setParentId(parentId);
            entity.setName(name);
            entity.setOrderNum(orderNum);
            entity.setDelFlag(delFlag);
            entity.setDescription(description);
            entity.setLinkman(linkman);
            entity.setContact(contact);
            entity.setPhone(phone);
            entity.setEmail(email);
            entity.setStatus(status);
            entity.setLogoPath(dept.getLogoPath());
            entity.setCustomLogin(dept.isCustomLogin());

            deptService.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改保存部门
     */
    @SysLog("更新部门")
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    public R editSave(@RequestBody DeptEntity dept) {
        MessageBundle messageBundle = getMessageBundle();
        BeanUtil.beanAttributeValueTrim(dept);
        deptUpdateValidator.validate(dept, messageBundle);

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            DeptEntity entity = deptService.findById(dept.getId());
            String no = dept.getNo();
            String parentId = dept.getParentId();
            String name = dept.getName();
            Integer orderNum = dept.getOrderNum();
            Integer delFlag = dept.getDelFlag();
            String description = dept.getDescription();
            String linkman = dept.getLinkman();
            String contact = dept.getContact();
            String phone = dept.getPhone();
            String email = dept.getEmail();
            String status = dept.getStatus();

            entity.setNo(no);
            entity.setParentId(parentId);
            entity.setName(name);
            entity.setOrderNum(orderNum);
            entity.setDelFlag(delFlag);
            entity.setDescription(description);
            entity.setLinkman(linkman);
            entity.setContact(contact);
            entity.setPhone(phone);
            entity.setEmail(email);
            entity.setStatus(status);
            entity.setLogoPath(dept.getLogoPath());
            entity.setCustomLogin(dept.isCustomLogin());

            deptService.update(entity);
            return R.ok();
        }
    }

    /**
     * 删除部门
     */
    @SysLog("删除部门")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public R remove(@RequestParam("ids") String ids) {
        if (StringUtils.isEmpty(ids)) {
            return R.error("删除对象不能为空");
        }
        deptService.deleteByIds(ids);
        return R.ok();
    }

    @SysLog("部门Logo上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseRuntimeException("上传文件不能为空");
        }
        StringBuffer url = new StringBuffer();
        String dir = DateUtil.formatDate(new Date(), "yyyyMMdd");
        String sep = "/";
        url.append("/file").append(sep).append(dir);
        String fileDir = uploadDownloadHelper.makePublicFileDir(url.toString());
        String originalFilename = file.getOriginalFilename();
        String suffix = uploadDownloadHelper.getSuffix(originalFilename);
        String fileType = uploadDownloadHelper.getFileType(originalFilename);
        String fileName = IdGenerator.getInstance().nextStringId() + suffix;
        String filePath = fileDir + sep + fileName;
        url.append(sep).append(fileName);

        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        File f = new File(filePath);
        Thumbnails.of(bis).size(900, 900).outputFormat(fileType).toFile(f);

        return R.ok("/upload" + url);
    }
    /**
     * 获取详细信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public R<DeptEntity> detail(@PathVariable String id) {
        DeptEntity entity = deptService.findById(id);
        if (entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        } else {
            return R.error("对象不存在");
        }
    }

    public static class SearchForm extends BaseSearchForm {
        private String status;
        private String name;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
