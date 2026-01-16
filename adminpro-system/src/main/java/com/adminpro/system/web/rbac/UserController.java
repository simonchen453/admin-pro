package com.adminpro.system.web.rbac;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.base.web.BaseSearchForm;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.annotation.SysLog;
import com.adminpro.system.core.common.helper.ExcelHelper;
import com.adminpro.system.core.common.helper.FileHelper;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.api.PasswordValidator;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.system.rbac.domains.entity.dept.DeptService;
import com.adminpro.system.rbac.domains.entity.post.PostEntity;
import com.adminpro.system.rbac.domains.entity.post.PostService;
import com.adminpro.system.rbac.domains.entity.role.RoleEntity;
import com.adminpro.system.rbac.domains.entity.role.RoleService;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.domains.entity.user.UserIden;
import com.adminpro.system.rbac.domains.entity.user.UserService;
import com.adminpro.system.rbac.domains.entity.userpost.UserPostAssignEntity;
import com.adminpro.system.rbac.domains.entity.userpost.UserPostAssignService;
import com.adminpro.system.rbac.domains.entity.userrole.UserRoleAssignEntity;
import com.adminpro.system.rbac.domains.entity.userrole.UserRoleAssignService;
import com.adminpro.system.rbac.domains.vo.user.*;
import com.adminpro.system.rbac.enums.UserStatus;
import com.adminpro.system.tools.domains.entity.oss.OSSEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UserController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:user')")
public class UserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    protected static final String PREFIX_URL = "/admin/user";
    protected static final String SEARCH_FORM_KEY = "userSearchForm";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleAssignService userRoleAssignService;

    @Autowired
    private UserPostAssignService userPostAssignService;

    @Autowired
    private UserCreateValidator userCreateValidator;

    @Autowired
    private UserUpdateValidator userUpdateValidator;

    @Autowired
    private FileHelper fileHelper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private PostService postService;

    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public R<QueryResultSet<UserListResponseVo>> search(@RequestBody SearchForm searchForm) {
        logger.debug("查询用户列表: searchForm={}", searchForm);
        SearchParam param = buildSearchParam(searchForm);
        QueryResultSet<UserEntity> search = userService.search(param);

        List<UserListResponseVo> list = new ArrayList<>();
        if (search.getRecords() != null) {
            for (UserEntity entity : search.getRecords()) {
                UserListResponseVo vo = new UserListResponseVo();
                BeanUtils.copyProperties(entity, vo);
                vo.setUserId(entity.getId());
                list.add(vo);
            }
        }

        QueryResultSet<UserListResponseVo> result = new QueryResultSet<>();
        result.setRecords(list);
        result.setTotalCount(search.getTotalCount());
        result.setCurrentPage(search.getCurrentPage());
        result.setPageSize(search.getPageSize());
        result.setTotalPage(search.getTotalPage());

        logger.debug("查询用户列表成功: count={}", search.getTotalCount());
        return R.ok(result);
    }

    @SysLog("停用用户")
    @RequestMapping(value = "/inactive/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PATCH)
    public R inactive(@PathVariable String userId) {
        logger.info("停用用户: userId={}", userId);
        UserEntity userEntity = userService.findById(userId);
        if (userEntity == null) {
            logger.warn("用户不存在: userId={}", userId);
            return R.error(RbacConstants.MSG_USER_NOT_FOUND);
        }
        userEntity.setStatus(UserStatus.INACTIVE.getCode());
        userService.update(userEntity);
        logger.info("停用用户成功: userId={}", userId);
        return R.ok();
    }

    @SysLog("激活用户")
    @RequestMapping(value = "/active/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PATCH)
    public R active(@PathVariable("userId") String userId) {
        logger.info("激活用户: userId={}", userId);
        UserEntity userEntity = userService.findById(userId);
        if (userEntity == null) {
            logger.warn("用户不存在: userId={}", userId);
            return R.error(RbacConstants.MSG_USER_NOT_FOUND);
        }
        userEntity.setStatus(UserStatus.ACTIVE.getCode());
        userService.update(userEntity);
        logger.info("激活用户成功: userId={}", userId);
        return R.ok();
    }

    @SysLog("重置用户密码")
    @RequestMapping(value = "/resetpwd", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R resetPwd(@Valid @RequestBody UserResetPwdRequestVo userResetPwdRequestVo) {
        logger.info("重置用户密码: userDomain={}, userId={}", userResetPwdRequestVo.getUserDomain(),
                userResetPwdRequestVo.getUserId());
        BeanUtil.beanAttributeValueTrim(userResetPwdRequestVo);
        String userId = userResetPwdRequestVo.getUserId();
        String userDomain = userResetPwdRequestVo.getUserDomain();
        String newPassword = userResetPwdRequestVo.getNewPassword();
        String confirmPassword = userResetPwdRequestVo.getConfirmPassword();

        MessageBundle messageBundle = getMessageBundle();
        if (StringUtils.isEmpty(newPassword)) {
            messageBundle.addErrorMessage("newPassword", RbacConstants.MSG_PASSWORD_NOT_EMPTY);
        }
        if (StringUtils.isEmpty(confirmPassword)) {
            messageBundle.addErrorMessage("confirmPassword", RbacConstants.MSG_CONFIRM_PASSWORD_NOT_EMPTY);
        }
        if (!StringUtils.equals(newPassword, confirmPassword)) {
            messageBundle.addErrorMessage("confirmPassword", RbacConstants.MSG_PASSWORD_NOT_MATCH);
        }
        try {
            if (StringUtils.isNotEmpty(newPassword)) {
                List<String> passwordErrors = PasswordValidator.validatePassword(newPassword);
                if (passwordErrors != null && !passwordErrors.isEmpty()) {
                    messageBundle.addErrorMessage("newPassword", String.join("；", passwordErrors));
                }
            }
            if (!messageBundle.hasErrorMessage()) {
                // userId 是主键ID，需要先获取用户再构造 UserIden
                UserEntity userEntity = userService.findById(userId);
                if (userEntity == null) {
                    logger.warn("重置用户密码失败，用户不存在: userId={}", userId);
                    return R.error(RbacConstants.MSG_USER_NOT_FOUND);
                }
                userService.resetPwd(new UserIden(userEntity.getUserDomain(), userEntity.getLoginName()), newPassword);
                logger.info("重置用户密码成功: userDomain={}, userId={}", userDomain, userId);
                return R.ok();
            } else {
                logger.warn("重置用户密码验证失败: userDomain={}, userId={}, errors={}", userDomain, userId,
                        messageBundle.getErrorMessages());
                return R.error(messageBundle);
            }
        } catch (Exception e) {
            logger.error("重置用户密码失败: userDomain={}, userId={}", userDomain, userId, e);
            return R.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/detail/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public R<UserDetailVO> view(@PathVariable String userId) {
        logger.debug("查询用户详情: userId={}", userId);
        UserEntity userEntity = userService.findById(userId);
        if (userEntity == null) {
            logger.warn("用户不存在: userId={}", userId);
            return R.error(RbacConstants.MSG_USER_NOT_FOUND);
        }

        UserDetailVO sysUserResponseVo = new UserDetailVO();
        sysUserResponseVo.setLoginName(userEntity.getLoginName());
        sysUserResponseVo.setAvatarUrl(userEntity.getAvatarUrl());
        sysUserResponseVo.setRealName(userEntity.getRealName());
        sysUserResponseVo.setMobileNo(userEntity.getMobileNo());
        if (userEntity != null) {
            sysUserResponseVo.setUserDomain(userEntity.getUserDomain());
            sysUserResponseVo.setUserId(userEntity.getId());
        }
        sysUserResponseVo.setDescription(userEntity.getDescription());
        sysUserResponseVo.setLatestLoginTime(userEntity.getLatestLoginTime());
        sysUserResponseVo.setStatus(userEntity.getStatus());
        sysUserResponseVo.setEmail(userEntity.getEmail());
        sysUserResponseVo.setSex(userEntity.getSex());

        String deptNo = userEntity.getDeptNo();
        sysUserResponseVo.setDeptNo(deptNo);
        if (StringUtils.isNotEmpty(deptNo)) {
            DeptEntity deptEntity = deptService.findByNo(deptNo);
            if (deptEntity != null) {
                sysUserResponseVo.setDeptId(deptEntity.getId());
            }
        }

        // 批量查询角色（优化N+1问题）
        List<UserRoleAssignEntity> assignedRoles = userRoleAssignService.findByUserId(userEntity.getId());
        if (assignedRoles != null && !assignedRoles.isEmpty()) {
            List<String> roleIds = assignedRoles.stream()
                    .map(UserRoleAssignEntity::getRoleId)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            sysUserResponseVo.setRoleIds(roleIds);
        } else {
            sysUserResponseVo.setRoleIds(new ArrayList<>());
        }

        // 批量查询岗位（优化N+1问题）
        List<UserPostAssignEntity> assignPosts = userPostAssignService.findByUserId(userEntity.getId());
        if (assignPosts != null && !assignPosts.isEmpty()) {
            List<String> postIds = assignPosts.stream()
                    .map(UserPostAssignEntity::getPostId)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            sysUserResponseVo.setPostIds(postIds);
        } else {
            sysUserResponseVo.setPostIds(new ArrayList<>());
        }

        logger.debug("查询用户详情成功: userId={}", userId);
        return R.ok(sysUserResponseVo);
    }

    @SysLog("删除用户")
    @RequestMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public R deleteMany(@RequestParam String users) {
        logger.info("批量删除用户: users={}", users);
        try {
            // 使用验证工具类解析和验证参数
            List<String> userIds = BatchOperationValidator.validateAndParseIds(users);
            userService.deleteMany(StringUtils.join(userIds, ","));
            logger.info("批量删除用户成功: count={}", userIds.size());
            return R.ok();
        } catch (Exception e) {
            logger.error("批量删除用户失败: users={}", users, e);
            return R.error(e);
        }
    }

    /**
     * 批量删除用户 (RESTful 风格，使用 List 参数)
     * 推荐使用此接口，更符合 RESTful 规范
     * 
     * @param userIds 用户ID列表，格式: ["userDomain_userId", ...]
     * @return 操作结果
     */
    @SysLog("删除用户")
    @RequestMapping(value = "/batch-delete", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public R batchDelete(@RequestBody List<String> userIds) {
        logger.info("批量删除用户(RESTful): count={}", userIds != null ? userIds.size() : 0);
        try {
            if (userIds == null || userIds.isEmpty()) {
                return R.error("用户ID列表不能为空");
            }
            userService.deleteMany(StringUtils.join(userIds, ","));
            logger.info("批量删除用户成功: count={}", userIds.size());
            return R.ok();
        } catch (Exception e) {
            logger.error("批量删除用户失败: userIds={}", userIds, e);
            return R.error(e);
        }
    }

    @RequestMapping(value = "/prepare", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public R<Map<String, Object>> prepare() {
        Map<String, Object> map = new HashMap<>();
        SearchParam postSearchParam = startPaging();
        SearchParam roleSearchParam = startPaging();

        List<PostEntity> posts = postService.findByParam(postSearchParam);
        List<RoleEntity> roles = roleService.findByParam(roleSearchParam);

        map.put("posts", posts);
        map.put("roles", roles);
        return R.ok(map);
    }

    @SysLog("创建用户")
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @Transactional
    public R create(@Valid @RequestBody UserCreateVo userRequestVo) {
        logger.info("创建用户: loginName={}, userDomain={}", userRequestVo.getLoginName(), userRequestVo.getUserDomain());
        BeanUtil.beanAttributeValueTrim(userRequestVo);
        MessageBundle messageBundle = getMessageBundle();
        userCreateValidator.validate(userRequestVo, messageBundle);
        if (!messageBundle.hasErrorMessage()) {
            UserEntity user = buildUserEntity(userRequestVo);
            userService.create(user);

            assignRolesToUser(user, userRequestVo.getRoleIds());
            assignPostsToUser(user, userRequestVo.getPostIds());

            logger.info("创建用户成功: loginName={}, userDomain={}, userId={}",
                    userRequestVo.getLoginName(), userRequestVo.getUserDomain(), user.getId());
            return R.ok();
        } else {
            logger.warn("创建用户验证失败: loginName={}, errors={}", userRequestVo.getLoginName(),
                    messageBundle.getErrorMessages());
            return R.error(messageBundle);
        }
    }

    @SysLog("更新用户")
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PATCH)
    @Transactional
    public R update(@Valid @RequestBody UserCreateVo userRequestVo) {
        logger.info("更新用户: userDomain={}, userId={}", userRequestVo.getUserDomain(), userRequestVo.getUserId());
        BeanUtil.beanAttributeValueTrim(userRequestVo);
        MessageBundle messageBundle = getMessageBundle();
        userUpdateValidator.validate(userRequestVo, messageBundle);
        if (!messageBundle.hasErrorMessage()) {
            // userId 是主键ID，直接使用 findById
            UserEntity userEntity = userService.findById(userRequestVo.getUserId());
            if (userEntity == null) {
                logger.warn("更新用户失败，用户不存在: userDomain={}, userId={}", userRequestVo.getUserDomain(),
                        userRequestVo.getUserId());
                return R.error(RbacConstants.MSG_USER_NOT_FOUND);
            }

            updateUserEntity(userEntity, userRequestVo);
            userService.update(userEntity);

            userRoleAssignService.deleteByUserId(userEntity.getId());
            assignRolesToUser(userEntity, userRequestVo.getRoleIds());

            userPostAssignService.deleteByUserId(userEntity.getId());
            assignPostsToUser(userEntity, userRequestVo.getPostIds());

            logger.info("更新用户成功: userDomain={}, userId={}", userRequestVo.getUserDomain(), userRequestVo.getUserId());
            return R.ok();
        } else {
            logger.warn("更新用户验证失败: userDomain={}, userId={}, errors={}",
                    userRequestVo.getUserDomain(), userRequestVo.getUserId(), messageBundle.getErrorMessages());
            return R.error(messageBundle);
        }
    }

    @SysLog("用户头像上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uploadFile(@RequestParam MultipartFile file, MultipartHttpServletRequest multipartRequest) {
        try {
            OSSEntity upload = fileHelper.uploadOssFile(file);
            return R.ok(upload.getUrl());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.error(e);
        }
    }

    @SysLog("导入用户")
    @RequestMapping(value = "/import", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R importUser(@RequestParam("file") MultipartFile file) {
        logger.info("导入用户: fileName={}, fileSize={}", file.getOriginalFilename(), file.getSize());
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            List<UserImportVo> importList = ExcelHelper.importExcel(file.getInputStream(), UserImportVo.class, params);
            userService.importExcel(importList);
            logger.info("导入用户成功: fileName={}, count={}", file.getOriginalFilename(), importList.size());
            return R.ok();
        } catch (Exception e) {
            logger.error("导入用户失败: fileName={}", file.getOriginalFilename(), e);
            return R.error(RbacConstants.MSG_USER_IMPORT_FAILED + e.getMessage());
        }
    }

    @SysLog("导出用户")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportUser(@RequestParam(required = false) String ids, HttpServletResponse response) throws Exception {
        List<UserEntity> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(ids)) {
            // 使用验证工具类解析和验证参数
            List<String> userIds = BatchOperationValidator.validateAndParseIds(ids);
            // 使用批量查询优化 N+1 问题
            List<UserEntity> users = userService.findByIds(userIds);
            list.addAll(users);
        }
        userService.exportExcel(response, list);
    }

    @SysLog("导出所有用户")
    @RequestMapping(value = "/excelAll", method = RequestMethod.GET)
    public void exportAllUser(SearchForm searchForm, HttpServletResponse response) throws Exception {
        logger.info("导出所有用户: searchForm={}", searchForm);
        SearchParam param = buildSearchParam(searchForm);
        List<UserEntity> list = userService.findByParam(param);
        userService.exportExcel(response, list);
        logger.info("导出所有用户成功: count={}", list.size());
    }

    /**
     * 构建搜索参数（提取重复代码）
     */
    private SearchParam buildSearchParam(SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        SearchParam param = startPaging(searchForm);
        setSearchForm(request, searchForm);

        if (StringUtils.isNotEmpty(searchForm.getUserDomain())) {
            param.addFilter("userDomain", searchForm.getUserDomain());
        }
        if (StringUtils.isNotEmpty(searchForm.getLoginName())) {
            param.addFilter("loginName", searchForm.getLoginName());
        }
        if (StringUtils.isNotEmpty(searchForm.getRealName())) {
            param.addFilter("realName", searchForm.getRealName());
        }
        if (StringUtils.isNotEmpty(searchForm.getStatus())) {
            param.addFilter("status", searchForm.getStatus());
        }
        if (StringUtils.isNotEmpty(searchForm.getDeptId())) {
            param.addFilter("deptId", searchForm.getDeptId());
        }
        return param;
    }

    /**
     * 构建用户实体（提取重复代码）
     */
    private UserEntity buildUserEntity(UserCreateVo userRequestVo) {
        UserEntity user = new UserEntity();
        user.setLoginName(userRequestVo.getLoginName());
        user.setUserDomain(userRequestVo.getUserDomain());
        user.setId(StringUtils.isNotEmpty(userRequestVo.getUserId()) ? userRequestVo.getUserId()
                : IdGenerator.getInstance().nextStringId());
        user.setStatus(userRequestVo.getStatus());
        user.setRealName(userRequestVo.getRealName());
        user.setDescription(userRequestVo.getDescription());
        user.setAvatarUrl(userRequestVo.getAvatarUrl());
        user.setMobileNo(userRequestVo.getMobileNo());
        user.setPassword(userRequestVo.getPassword());
        user.setSex(userRequestVo.getSex());
        user.setEmail(userRequestVo.getEmail());

        String deptId = userRequestVo.getDeptId();
        if (StringUtils.isNotEmpty(deptId)) {
            DeptEntity deptEntity = deptService.findById(deptId);
            if (deptEntity != null) {
                user.setDeptNo(deptEntity.getNo());
            }
        }
        return user;
    }

    /**
     * 更新用户实体（提取重复代码）
     */
    private void updateUserEntity(UserEntity userEntity, UserCreateVo userRequestVo) {
        userEntity.setRealName(userRequestVo.getRealName());
        userEntity.setLoginName(userRequestVo.getLoginName());
        userEntity.setMobileNo(userRequestVo.getMobileNo());
        userEntity.setDescription(userRequestVo.getDescription());
        userEntity.setAvatarUrl(userRequestVo.getAvatarUrl());
        userEntity.setStatus(userRequestVo.getStatus());
        userEntity.setSex(userRequestVo.getSex());
        userEntity.setEmail(userRequestVo.getEmail());

        String deptId = userRequestVo.getDeptId();
        if (StringUtils.isNotEmpty(deptId)) {
            DeptEntity deptEntity = deptService.findById(deptId);
            if (deptEntity != null) {
                userEntity.setDeptNo(deptEntity.getNo());
            }
        }
    }

    /**
     * 分配角色给用户（提取重复代码，优化批量查询）
     */
    private void assignRolesToUser(UserEntity user, List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        List<RoleEntity> roleEntities = roleService.findByIds(roleIds);
        Map<String, RoleEntity> roleMap = roleEntities.stream()
                .collect(Collectors.toMap(RoleEntity::getId, role -> role));

        for (String roleId : roleIds) {
            RoleEntity roleEntity = roleMap.get(roleId);
            if (roleEntity != null) {
                UserRoleAssignEntity assignEntity = new UserRoleAssignEntity();
                assignEntity.setUserId(user.getId());
                assignEntity.setRoleId(roleEntity.getId());
                userRoleAssignService.create(assignEntity);
            }
        }
    }

    /**
     * 分配岗位给用户（提取重复代码，优化批量查询）
     */
    private void assignPostsToUser(UserEntity user, List<String> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return;
        }

        List<PostEntity> postEntities = postService.findByIds(postIds);
        Map<String, PostEntity> postMap = postEntities.stream()
                .collect(Collectors.toMap(PostEntity::getId, post -> post));

        for (String postId : postIds) {
            PostEntity postEntity = postMap.get(postId);
            if (postEntity != null) {
                UserPostAssignEntity assignEntity = new UserPostAssignEntity();
                assignEntity.setUserId(user.getId());
                assignEntity.setPostId(postEntity.getId());
                userPostAssignService.create(assignEntity);
            }
        }
    }

    public static class SearchForm extends BaseSearchForm {
        private String userDomain;
        private String userId;
        private String display;
        private String status;
        private String loginName;
        private String realName;
        private String deptId;

        public String getUserDomain() {
            return userDomain;
        }

        public void setUserDomain(String userDomain) {
            this.userDomain = userDomain;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        @Override
        public String toString() {
            return "SearchForm{" +
                    "userDomain='" + userDomain + '\'' +
                    ", userId='" + userId + '\'' +
                    ", display='" + display + '\'' +
                    ", status='" + status + '\'' +
                    ", loginName='" + loginName + '\'' +
                    ", realName='" + realName + '\'' +
                    ", deptId='" + deptId + '\'' +
                    '}';
        }
    }

    public static SearchForm getSearchForm(HttpServletRequest request) {
        SearchForm searchForm = (SearchForm) request.getSession().getAttribute(SEARCH_FORM_KEY);
        if (searchForm == null) {
            searchForm = new SearchForm();
        }
        setSearchForm(request, searchForm);
        return searchForm;
    }

    public static void setSearchForm(HttpServletRequest request, SearchForm searchForm) {
        request.getSession().setAttribute(SEARCH_FORM_KEY, searchForm);
    }

    public static void cleanSearchForm(HttpServletRequest request) {
        request.getSession().removeAttribute(SEARCH_FORM_KEY);
    }
}
