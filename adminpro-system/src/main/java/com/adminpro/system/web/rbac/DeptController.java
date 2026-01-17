package com.adminpro.system.web.rbac;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.*;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.exceptions.BaseRuntimeException;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.helper.FileHelper;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.domains.entity.dept.DeptCreateValidator;
import com.adminpro.system.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.system.rbac.domains.entity.dept.DeptService;
import com.adminpro.system.rbac.domains.entity.dept.DeptUpdateValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * 部门管理控制器
 * <p>
 * 提供部门的增删改查功能，包括部门列表查询、部门详情查询、部门创建、部门更新、部门删除、Logo上传等操作
 * </p>
 *
 * @author simon
 * @date 2020-05-24
 */
@Tag(name = "部门管理", description = "部门的增删改查接口")
@RestController
@RequestMapping(DeptController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:dept')")
public class DeptController extends BaseController {
    protected static final String PREFIX_URL = "/api/v1/departments";
    protected static final String SEARCH_FORM_KEY = "deptSearchForm";

    @Autowired
    private FileHelper fileHelper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private DeptCreateValidator deptCreateValidator;

    @Autowired
    private DeptUpdateValidator deptUpdateValidator;

    /**
     * 查询部门列表
     * <p>
     * 根据查询条件获取部门列表，支持按名称、状态等条件进行过滤和分页查询
     * </p>
     *
     * @param searchForm 查询条件表单，包含部门名称、状态等过滤条件
     * @return 部门列表
     */
    @Operation(summary = "查询部门列表", description = "根据查询条件获取部门列表，支持按名称、状态等条件进行过滤和分页查询")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeptEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping
    public R<List<DeptEntity>> list(
            @Parameter(description = "查询条件表单", required = true) @RequestBody SearchForm searchForm) {
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
     * 创建部门
     * <p>
     * 新增一个部门，包含部门编号、名称、上级部门、联系人等信息
     * </p>
     *
     * @param dept 部门实体信息
     * @return 操作结果
     */
    @Operation(summary = "创建部门", description = "新增一个部门，包含部门编号、名称、上级部门、联系人等信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @SysLog("创建部门")
    @PostMapping(value = "/create")
    public R create(@Parameter(description = "部门实体信息", required = true) @RequestBody DeptEntity dept) {
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
     * 更新部门
     * <p>
     * 更新已有部门的信息
     * </p>
     *
     * @param dept 部门实体信息
     * @return 操作结果
     */
    @Operation(summary = "更新部门", description = "更新已有部门的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "部门不存在")
    })
    @SysLog("更新部门")
    @PutMapping(value = "/{id}")
    public R editSave(@Parameter(description = "部门实体信息", required = true) @RequestBody DeptEntity dept) {
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
     * 批量删除部门
     * <p>
     * 根据多个部门ID批量删除部门，ID之间用逗号分隔
     * </p>
     *
     * @param ids 部门ID列表，多个ID用逗号分隔
     * @return 操作结果
     */
    @Operation(summary = "批量删除部门", description = "根据多个部门ID批量删除部门，ID之间用逗号分隔")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @SysLog("删除部门")
    @DeleteMapping
    public R remove(
            @Parameter(description = "部门ID列表，多个ID用逗号分隔", required = true, example = "1,2,3") @RequestParam("ids") String ids) {
        // 使用验证工具类解析和验证参数
        List<String> deptIdList = BatchOperationValidator.validateAndParseIds(ids);
        deptService.deleteByIds(StringUtils.join(deptIdList, ","));
        return R.ok();
    }

    /**
     * 上传部门Logo
     * <p>
     * 上传部门Logo图片文件，支持常见图片格式，自动压缩至900x900以内
     * </p>
     *
     * @param file Logo图片文件
     * @return Logo文件的访问URL
     * @throws Exception 上传过程中可能出现的异常
     */
    @Operation(summary = "上传部门Logo", description = "上传部门Logo图片文件，支持常见图片格式，自动压缩至900x900以内")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "文件为空或格式错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @SysLog("部门Logo上传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uploadFile(@Parameter(description = "Logo图片文件", required = true) @RequestParam("file") MultipartFile file)
            throws Exception {
        if (file.isEmpty()) {
            throw new BaseRuntimeException("上传文件不能为空");
        }
        StringBuffer url = new StringBuffer();
        String dir = DateUtil.formatDate(new Date(), "yyyyMMdd");
        String sep = "/";
        url.append("/file").append(sep).append(dir);
        String fileDir = fileHelper.makePublicFileDir(url.toString());
        String originalFilename = file.getOriginalFilename();
        String suffix = fileHelper.getSuffix(originalFilename);
        String fileType = fileHelper.getFileType(originalFilename);
        String fileName = IdGenerator.getInstance().nextStringId() + suffix;
        String filePath = fileDir + sep + fileName;
        url.append(sep).append(fileName);

        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        File f = new File(filePath);
        Thumbnails.of(bis).size(900, 900).outputFormat(fileType).toFile(f);

        return R.ok(FileUtil.FILE_URL_PREFIX + url);
    }

    /**
     * 查询部门详情
     * <p>
     * 根据部门ID获取部门的详细信息
     * </p>
     *
     * @param id 部门ID
     * @return 部门详细信息
     */
    @Operation(summary = "查询部门详情", description = "根据部门ID获取部门的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeptEntity.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "部门不存在")
    })
    @GetMapping(value = "/{id}")
    public R<DeptEntity> detail(@Parameter(description = "部门ID", required = true) @PathVariable String id) {
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

        @Override
        public String toString() {
            return "SearchForm{" +
                    "status='" + status + '\'' +
                    ", name='" + name + '\'' +
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
