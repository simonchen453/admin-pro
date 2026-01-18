package com.adminpro.system.web.rbac;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.BeanUtil;
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
import com.adminpro.system.rbac.domains.entity.user.UserService;
import com.adminpro.system.rbac.domains.entity.userpost.UserPostAssignEntity;
import com.adminpro.system.rbac.domains.entity.userpost.UserPostAssignService;
import com.adminpro.system.rbac.domains.entity.userrole.UserRoleAssignEntity;
import com.adminpro.system.rbac.domains.entity.userrole.UserRoleAssignService;
import com.adminpro.system.rbac.domains.vo.user.*;
import com.adminpro.system.rbac.enums.UserStatus;
import com.adminpro.system.tools.domains.entity.oss.OSSEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 * <p>
 * 提供用户的增删改查、导入导出、状态管理等功能
 * </p>
 *
 * @author system
 * @since 1.0.0
 */
@Tag(name = "用户管理", description = "用户的增删改查、导入导出、状态管理等接口")
@RestController
@RequestMapping(UserController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:user')")
/**
 * 使用 Lombok @RequiredArgsConstructor 自动生成构造器进行依赖注入。
 * 所有 final 字段将通过构造器自动注入，无需显式编写 @Autowired。
 * 添加新依赖时，只需添加 private final 字段即可。
 */
@RequiredArgsConstructor
public class UserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    protected static final String PREFIX_URL = "/api/v1/users";
    protected static final String SEARCH_FORM_KEY = "userSearchForm";

    private final UserService userService;
    private final UserRoleAssignService userRoleAssignService;
    private final UserPostAssignService userPostAssignService;
    private final UserCreateValidator userCreateValidator;
    private final UserUpdateValidator userUpdateValidator;
    private final FileHelper fileHelper;
    private final RoleService roleService;
    private final DeptService deptService;
    private final PostService postService;

    /**
     * 查询用户列表
     * <p>
     * 根据查询条件分页查询用户列表，支持按用户域、登录名、真实姓名、状态、部门等条件筛选
     * </p>
     *
     * @param searchForm 查询条件表单
     * @return 分页查询结果
     */
    @Operation(summary = "查询用户列表", description = "根据查询条件分页查询用户列表，支持按用户域、登录名、真实姓名、状态、部门等条件筛选")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 QueryResultSet<UserListResponseVo> 列表
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<QueryResultSet<UserListResponseVo>> search(@RequestBody SearchForm searchForm) {
        logger.debug("查询用户列表: searchForm={}", searchForm);
        SearchParam param = buildSearchParam(searchForm);
        QueryResultSet<UserEntity> search = userService.search(param);

        List<UserListResponseVo> list = new ArrayList<>();
        if (search.getRecords() != null) {
            for (UserEntity entity : search.getRecords()) {
                UserListResponseVo vo = new UserListResponseVo();
                BeanUtils.copyProperties(entity, vo);
                vo.setId(entity.getId());
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

    /**
     * 停用用户
     * <p>
     * 将指定用户状态设置为停用，停用后用户无法登录系统
     * </p>
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @SysLog("停用用户")
    @Operation(summary = "停用用户", description = "将指定用户状态设置为停用，停用后用户无法登录系统")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 停用成功
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=404: 用户不存在
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
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

    /**
     * 激活用户
     * <p>
     * 将指定用户状态设置为激活，激活后用户可以正常登录系统
     * </p>
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @SysLog("激活用户")
    @Operation(summary = "激活用户", description = "将指定用户状态设置为激活，激活后用户可以正常登录系统")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 激活成功
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=404: 用户不存在
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
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

    /**
     * 重置用户密码
     * <p>
     * 管理员重置指定用户的密码，新密码需符合系统密码规则
     * </p>
     *
     * @param userResetPwdRequestVo 密码重置请求参数，包含用户ID、新密码、确认密码
     * @return 操作结果
     */
    @SysLog("重置用户密码")
    @Operation(summary = "重置用户密码", description = "管理员重置指定用户的密码，新密码需符合系统密码规则")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 重置成功
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @RequestMapping(value = "/resetpwd", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R resetPwd(@Valid @RequestBody UserResetPwdRequestVo userResetPwdRequestVo) {
        logger.info("重置用户密码: userDomain={}, userId={}", userResetPwdRequestVo.getUserDomain(),
                userResetPwdRequestVo.getId());
        BeanUtil.beanAttributeValueTrim(userResetPwdRequestVo);
        String userId = userResetPwdRequestVo.getId();
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
                // userId 是主键ID，需要先获取用户
                UserEntity userEntity = userService.findById(userId);
                if (userEntity == null) {
                    logger.warn("重置用户密码失败，用户不存在: userId={}", userId);
                    return R.error(RbacConstants.MSG_USER_NOT_FOUND);
                }
                userService.resetPwd(userEntity.getUserDomain(), userEntity.getLoginName(), newPassword);
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

    /**
     * 查询用户详情
     * <p>
     * 根据用户ID查询用户的详细信息，包括基本资料、角色、岗位等
     * </p>
     *
     * @param userId 用户ID
     * @return 用户详细信息
     */
    @Operation(summary = "查询用户详情", description = "根据用户ID查询用户的详细信息，包括基本资料、角色、岗位等")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 UserDetailVO 对象
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=404: 用户不存在
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
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
        sysUserResponseVo.setUserDomain(userEntity.getUserDomain());
        sysUserResponseVo.setId(userEntity.getId());
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

    /**
     * 批量删除用户
     * <p>
     * 根据用户ID列表批量删除用户（使用逗号分隔的字符串格式）
     * </p>
     *
     * @param users 用户ID列表，逗号分隔，例如: "id1,id2,id3"
     * @return 操作结果
     */
    @SysLog("删除用户")
    @Operation(summary = "批量删除用户", description = "根据用户ID列表批量删除用户（使用逗号分隔的字符串格式）")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 批量删除成功
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public R<Void> deleteMany(@RequestParam String users) {
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
     * 批量删除用户 (RESTful风格)
     * <p>
     * 根据用户ID列表批量删除用户（使用JSON数组格式）
     * </p>
     *
     * @param userIds 用户ID列表，JSON数组格式: ["id1", "id2", "id3"]
     * @return 操作结果
     */
    @SysLog("删除用户")
    @Operation(summary = "批量删除用户(RESTful)", description = "根据用户ID列表批量删除用户（使用JSON数组格式）")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 批量删除成功
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
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

    /**
     * 准备用户创建/编辑页面数据
     * <p>
     * 获取创建或编辑用户时所需的基础数据，包括角色列表和岗位列表
     * </p>
     *
     * @return 包含角色列表和岗位列表的映射
     */
    @Operation(summary = "准备用户创建/编辑页面数据", description = "获取创建或编辑用户时所需的基础数据，包括角色列表和岗位列表")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含角色列表和岗位列表
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
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

    /**
     * 创建用户
     * <p>
     * 创建新用户，包括基本信息、角色分配、岗位分配等
     * </p>
     *
     * @param userRequestVo 用户创建请求参数
     * @return 操作结果
     */
    @SysLog("创建用户")
    @Operation(summary = "创建用户", description = "创建新用户，包括基本信息、角色分配、岗位分配等")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 创建成功
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
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

    /**
     * 更新用户
     * <p>
     * 更新用户基本信息、角色分配、岗位分配等
     * </p>
     *
     * @param userRequestVo 用户更新请求参数
     * @return 操作结果
     */
    @SysLog("更新用户")
    @Operation(summary = "更新用户", description = "更新用户基本信息、角色分配、岗位分配等")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 更新成功
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=404: 用户不存在
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @PutMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public R update(@Valid @RequestBody UserCreateVo userRequestVo) {
        logger.info("更新用户: userDomain={}, userId={}", userRequestVo.getUserDomain(), userRequestVo.getId());
        BeanUtil.beanAttributeValueTrim(userRequestVo);
        MessageBundle messageBundle = getMessageBundle();
        userUpdateValidator.validate(userRequestVo, messageBundle);
        if (!messageBundle.hasErrorMessage()) {
            // userId 是主键ID，直接使用 findById
            UserEntity userEntity = userService.findById(userRequestVo.getId());
            if (userEntity == null) {
                logger.warn("更新用户失败，用户不存在: userDomain={}, userId={}", userRequestVo.getUserDomain(),
                        userRequestVo.getId());
                return R.error(RbacConstants.MSG_USER_NOT_FOUND);
            }

            updateUserEntity(userEntity, userRequestVo);
            userService.update(userEntity);

            userRoleAssignService.deleteByUserId(userEntity.getId());
            assignRolesToUser(userEntity, userRequestVo.getRoleIds());

            userPostAssignService.deleteByUserId(userEntity.getId());
            assignPostsToUser(userEntity, userRequestVo.getPostIds());

            logger.info("更新用户成功: userDomain={}, userId={}", userRequestVo.getUserDomain(), userRequestVo.getId());
            return R.ok();
        } else {
            logger.warn("更新用户验证失败: userDomain={}, userId={}, errors={}",
                    userRequestVo.getUserDomain(), userRequestVo.getId(), messageBundle.getErrorMessages());
            return R.error(messageBundle);
        }
    }

    /**
     * 用户头像上传
     * <p>
     * 上传用户头像图片到OSS存储
     * </p>
     *
     * @param file             上传的文件
     * @param multipartRequest 多部分请求对象
     * @return OSS文件访问URL
     */
    @SysLog("用户头像上传")
    @Operation(summary = "用户头像上传", description = "上传用户头像图片到OSS存储")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 上传成功，data 字段包含图片URL
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 上传失败或服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
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

    /**
     * 导入用户
     * <p>
     * 从Excel文件批量导入用户数据
     * </p>
     *
     * @param file Excel文件
     * @return 操作结果
     */
    @SysLog("导入用户")
    @Operation(summary = "导入用户", description = "从Excel文件批量导入用户数据")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 导入成功
                - restCode=400: 参数错误或文件格式错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 导入失败或服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
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

    /**
     * 导出用户
     * <p>
     * 根据用户ID列表导出用户数据到Excel文件
     * </p>
     *
     * @param ids      用户ID列表，逗号分隔（可选）
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    @SysLog("导出用户")
    @Operation(summary = "导出用户", description = "根据用户ID列表导出用户数据到Excel文件")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 导出成功，返回Excel文件流
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 导出失败或服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
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

    /**
     * 导出所有用户
     * <p>
     * 根据查询条件导出所有符合条件的用户数据到Excel文件
     * </p>
     *
     * @param searchForm 查询条件表单
     * @param response   HTTP响应对象
     * @throws Exception 导出异常
     */
    @SysLog("导出所有用户")
    @Operation(summary = "导出所有用户", description = "根据查询条件导出所有符合条件的用户数据到Excel文件")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 导出成功，返回Excel文件流
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 导出失败或服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
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
        user.setId(StringUtils.isNotEmpty(userRequestVo.getId()) ? userRequestVo.getId()
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
