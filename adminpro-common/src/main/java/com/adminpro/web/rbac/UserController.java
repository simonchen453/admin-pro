package com.adminpro.web.rbac;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.BeanUtil;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.base.util.ValidationUtil;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.annotation.SysLog;
import com.adminpro.framework.common.helper.UploadDownloadHelper;
import com.adminpro.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.rbac.domains.entity.dept.DeptService;
import com.adminpro.rbac.domains.entity.domain.DomainEntity;
import com.adminpro.rbac.domains.entity.domain.DomainService;
import com.adminpro.rbac.domains.entity.post.PostEntity;
import com.adminpro.rbac.domains.entity.post.PostService;
import com.adminpro.rbac.domains.entity.role.RoleEntity;
import com.adminpro.rbac.domains.entity.role.RoleService;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.entity.user.UserIden;
import com.adminpro.rbac.domains.entity.user.UserService;
import com.adminpro.rbac.domains.entity.userpost.UserPostAssignEntity;
import com.adminpro.rbac.domains.entity.userpost.UserPostAssignService;
import com.adminpro.rbac.domains.entity.userrole.UserRoleAssignEntity;
import com.adminpro.rbac.domains.entity.userrole.UserRoleAssignService;
import com.adminpro.rbac.domains.vo.user.*;
import com.adminpro.rbac.enums.UserStatus;
import com.adminpro.tools.domains.entity.oss.OSSEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/user")
@PreAuthorize("@ss.hasPermission('system:user')")
public class UserController extends BaseRoutingController {
    protected static final String PREFIX = "admin/user";
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
    private UploadDownloadHelper uploadDownloadHelper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private PostService postService;

    @Autowired
    private DomainService domainService;

    @RequestMapping(method = RequestMethod.GET)
    public String user() {
        cleanSearchForm(request);
        return "forward:" + PREFIX_URL + "/list";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String user_list() {
        prepareData();
        getSearchForm(request);
        List<DomainEntity> all = domainService.findAll();
        request.setAttribute("domains", all);
        return PREFIX + "/list";
    }

    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public R<QueryResultSet<UserListResponseVo>> search(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        String status = searchForm.getStatus();
        String loginName = searchForm.getLoginName();
        String realName = searchForm.getRealName();
        String userDomain = searchForm.getUserDomain();
        String deptId = searchForm.getDeptId();

        SearchParam param = startPaging(searchForm);
        setSearchForm(request, searchForm);
        if (StringUtils.isNotEmpty(userDomain)) {
            param.addFilter("userDomain", userDomain);
        }

        if (StringUtils.isNotEmpty(loginName)) {
            param.addFilter("loginName", loginName);
        }

        if (StringUtils.isNotEmpty(realName)) {
            param.addFilter("realName", realName);
        }

        if (StringUtils.isNotEmpty(status)) {
            param.addFilter("status", status);
        }
        if (StringUtils.isNotEmpty(deptId)) {
            param.addFilter("deptId", deptId);
        }
        QueryResultSet<UserEntity> search = userService.search(param);

        return R.ok(search);
    }

    @SysLog("停用用户")
    @RequestMapping(value = "/inactive/{userDomain}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PATCH)
    @ResponseBody
    public R inactive(@PathVariable String userDomain, @PathVariable String userId) {
        UserEntity userEntity = userService.findByUserDomainAndUserId(userDomain, userId);
        if (userEntity != null) {
            userEntity.setStatus(UserStatus.LOCK.getCode());
            userService.update(userEntity);
            return R.ok();
        } else {
            return R.error("用户不存在");
        }
    }

    @SysLog("激活用户")
    @RequestMapping(value = "/active/{userDomain}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PATCH)
    @ResponseBody
    public R active(@PathVariable("userDomain") String userDomain, @PathVariable("userId") String userId) {
        UserEntity userEntity = userService.findByUserDomainAndUserId(userDomain, userId);
        if (userEntity != null) {
            userEntity.setStatus(UserStatus.ACTIVE.getCode());
            userService.update(userEntity);
            return R.ok();
        } else {
            return R.error("用户不存在");
        }
    }

    @SysLog("重置用户密码")
    @RequestMapping(value = "/resetpwd", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public R resetPwd(@RequestBody UserResetPwdRequestVo userResetPwdRequestVo) {
        BeanUtil.beanAttributeValueTrim(userResetPwdRequestVo);
        String userId = userResetPwdRequestVo.getUserId();
        String userDomain = userResetPwdRequestVo.getUserDomain();
        String newPassword = userResetPwdRequestVo.getNewPassword();
        String confirmPassword = userResetPwdRequestVo.getConfirmPassword();

        MessageBundle messageBundle = getMessageBundle();
        if (StringUtils.isEmpty(newPassword)) {
            messageBundle.addErrorMessage("newPassword", "新密码不能为空");
        }
        if (StringUtils.isEmpty(confirmPassword)) {
            messageBundle.addErrorMessage("confirmPassword", "确认密码不能为空");
        }
        if (!StringUtils.equals(newPassword, confirmPassword)) {
            messageBundle.addErrorMessage("confirmPassword", "两次密码不一致");
        }
        try {
            if (!ValidationUtil.isValidatePassword(newPassword)) {
                messageBundle.addErrorMessage("newPassword", "密码格式或强度不正确");
            }
            if (!messageBundle.hasErrorMessage()) {
                userService.resetPwd(new UserIden(userDomain, userId), newPassword);
                return R.ok();
            } else {
                return R.error(messageBundle);
            }
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/detail/{userDomain}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public R<UserDetailVO> view(@PathVariable String userDomain, @PathVariable String userId) {
        UserEntity userEntity = userService.findByUserDomainAndUserId(userDomain, userId);
        UserDetailVO sysUserResponseVo = new UserDetailVO();
        sysUserResponseVo.setLoginName(userEntity.getLoginName());
        sysUserResponseVo.setAvatarUrl(userEntity.getAvatarUrl());
        sysUserResponseVo.setRealName(userEntity.getRealName());
        sysUserResponseVo.setMobileNo(userEntity.getMobileNo());
        sysUserResponseVo.setUserDomain(userEntity.getUserIden().getUserDomain());
        sysUserResponseVo.setDescription(userEntity.getDescription());
        sysUserResponseVo.setUserId(userEntity.getUserIden().getUserId());
        sysUserResponseVo.setLatestLoginTime(userEntity.getLatestLoginTime());
        sysUserResponseVo.setStatus(userEntity.getStatus());
        sysUserResponseVo.setEmail(userEntity.getEmail());
        sysUserResponseVo.setDescription(userEntity.getDescription());
        String deptNo = userEntity.getDeptNo();
        sysUserResponseVo.setDeptNo(deptNo);
        DeptEntity deptEntity = deptService.findByNo(deptNo);
        if (deptEntity != null) {
            sysUserResponseVo.setDeptId(deptEntity.getId());
        }
        sysUserResponseVo.setPostIds(null);
        sysUserResponseVo.setRoleIds(null);
        sysUserResponseVo.setSex(userEntity.getSex());

        //user-role
        List<UserRoleAssignEntity> assignedRoles = userRoleAssignService.findByUserIden(userEntity.getUserIden());
        List<String> list = new ArrayList<>(assignedRoles.size());
        for (UserRoleAssignEntity assignedRole : assignedRoles) {
            RoleEntity roleEntity = roleService.findByName(assignedRole.getRoleName());
            list.add(roleEntity.getId());
        }

        //user-post
        List<UserPostAssignEntity> assignPosts = userPostAssignService.findByUserIden(userEntity.getUserIden());
        List<String> list2 = new ArrayList<>(assignPosts.size());
        for (UserPostAssignEntity assignEntity : assignPosts) {
            PostEntity postEntity = postService.findByCode(assignEntity.getPostCode());
            list2.add(postEntity.getId());
        }

        sysUserResponseVo.setRoleIds(list);
        sysUserResponseVo.setPostIds(list2);
        return R.ok(sysUserResponseVo);
    }

    @SysLog("删除用户")
    @RequestMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    @ResponseBody
    public R deleteMany(@RequestParam String users) {
        try {
            userService.deleteMany(users);
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }

    @RequestMapping(value = "/prepare", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
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
    @ResponseBody
    @Transactional
    public R create(@RequestBody UserCreateVo userRequestVo) {
        BeanUtil.beanAttributeValueTrim(userRequestVo);
        MessageBundle messageBundle = getMessageBundle();
        userCreateValidator.validate(userRequestVo, messageBundle);
        if (!messageBundle.hasErrorMessage()) {
            UserEntity user = new UserEntity();
            user.setLoginName(userRequestVo.getLoginName());
            user.setUserIden(new UserIden(userRequestVo.getUserDomain(), IdGenerator.getInstance().nextStringId()));
            user.setStatus(userRequestVo.getStatus());
            user.setRealName(userRequestVo.getRealName());
            user.setDescription(userRequestVo.getDescription());
            user.setAvatarUrl(userRequestVo.getAvatarUrl());
            user.setMobileNo(userRequestVo.getMobileNo());
            user.setPassword(userRequestVo.getPassword());
            user.setDescription(userRequestVo.getDescription());
            user.setSex(userRequestVo.getSex());
            user.setEmail(userRequestVo.getEmail());
            String deptId = userRequestVo.getDeptId();
            DeptEntity deptEntity = deptService.findById(deptId);
            if (deptEntity != null) {
                user.setDeptNo(deptEntity.getNo());
            }
            userService.create(user);

            //user-role
            List<String> roleIds = userRequestVo.getRoleIds();
            for (int i = 0; i < roleIds.size(); i++) {
                String roleId = roleIds.get(i);
                RoleEntity roleEntity = RoleService.getInstance().findById(roleId);
                if (roleEntity != null) {
                    UserRoleAssignEntity assignEntity = new UserRoleAssignEntity();
                    assignEntity.setUserDomain(user.getUserDomain());
                    assignEntity.setUserId(user.getUserId());
                    assignEntity.setRoleName(roleEntity.getName());
                    userRoleAssignService.create(assignEntity);
                }
            }

            //user-post
            List<String> postIds = userRequestVo.getPostIds();
            for (int i = 0; i < postIds.size(); i++) {
                String postId = postIds.get(i);
                PostEntity postEntity = PostService.getInstance().findById(postId);
                if (postEntity != null) {
                    UserPostAssignEntity assignEntity = new UserPostAssignEntity();
                    assignEntity.setUserDomain(user.getUserDomain());
                    assignEntity.setUserId(user.getUserId());
                    assignEntity.setPostCode(postEntity.getCode());
                    UserPostAssignService.getInstance().create(assignEntity);
                }
            }
            return R.ok();
        } else {
            return R.error(messageBundle);
        }
    }

    @SysLog("更新用户")
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PATCH)
    @ResponseBody
    @Transactional
    public R update(@RequestBody UserCreateVo userRequestVo) {
        BeanUtil.beanAttributeValueTrim(userRequestVo);
        MessageBundle messageBundle = getMessageBundle();
        userUpdateValidator.validate(userRequestVo, messageBundle);
        if (!messageBundle.hasErrorMessage()) {
            UserEntity userEntity = userService.findById(new UserIden(userRequestVo.getUserDomain(), userRequestVo.getUserId()));
            userEntity.setRealName(userRequestVo.getRealName());
            userEntity.setLoginName(userRequestVo.getLoginName());
            userEntity.setMobileNo(userRequestVo.getMobileNo());
            userEntity.setDescription(userRequestVo.getDescription());
            userEntity.setAvatarUrl(userRequestVo.getAvatarUrl());
            userEntity.setStatus(userRequestVo.getStatus());
            userEntity.setSex(userRequestVo.getSex());
            userEntity.setEmail(userRequestVo.getEmail());
            String deptId = userRequestVo.getDeptId();
            DeptEntity deptEntity = deptService.findById(deptId);
            if (deptEntity != null) {
                userEntity.setDeptNo(deptEntity.getNo());
            }
            userService.update(userEntity);

            //user-role
            userRoleAssignService.deleteByUserIden(userEntity.getUserIden());
            List<String> roleIds = userRequestVo.getRoleIds();
            for (int i = 0; i < roleIds.size(); i++) {
                String roleId = roleIds.get(i);
                RoleEntity roleEntity = RoleService.getInstance().findById(roleId);
                if (roleEntity != null) {
                    UserRoleAssignEntity assignEntity = new UserRoleAssignEntity();
                    assignEntity.setUserDomain(userEntity.getUserDomain());
                    assignEntity.setUserId(userEntity.getUserId());
                    assignEntity.setRoleName(roleEntity.getName());
                    userRoleAssignService.create(assignEntity);
                }
            }

            //user-post
            userPostAssignService.deleteByUserIden(userEntity.getUserIden());
            List<String> postIds = userRequestVo.getPostIds();
            for (int i = 0; i < postIds.size(); i++) {
                String postId = postIds.get(i);
                PostEntity postEntity = postService.findById(postId);
                if (postEntity != null) {
                    UserPostAssignEntity assignEntity = new UserPostAssignEntity();
                    assignEntity.setUserDomain(userEntity.getUserDomain());
                    assignEntity.setUserId(userEntity.getUserId());
                    assignEntity.setPostCode(postEntity.getCode());
                    userPostAssignService.create(assignEntity);
                }
            }
            return R.ok();
        } else {
            return R.error(messageBundle);
        }
    }

    @SysLog("用户头像上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public R uploadFile(@RequestParam MultipartFile file, MultipartHttpServletRequest multipartRequest) {
        try {
            OSSEntity upload = uploadDownloadHelper.uploadOssFile(file);
            return R.ok(upload.getUrl());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.error(e);
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
