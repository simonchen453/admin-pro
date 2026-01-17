package com.adminpro.system.rbac.domains.entity.user;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.CryptUtil;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.client.helper.ClientHelper;
import com.adminpro.framework.exceptions.BaseRuntimeException;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.common.helper.ConfigHelper;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.core.security.auth.TokenGenerator;
import com.adminpro.system.rbac.api.PasswordHelper;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.vo.user.UserExportVo;
import com.adminpro.system.rbac.domains.vo.user.UserImportVo;
import com.adminpro.system.rbac.enums.UserStatus;
import com.adminpro.system.web.BaseConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户服务类
 * <p>
 * 提供用户管理的核心业务功能，包括：
 * <ul>
 * <li>用户基本操作：创建、更新、删除、查询</li>
 * <li>用户认证：登录验证、密码验证、密码修改、密码重置</li>
 * <li>用户数据导入导出：Excel批量导入导出用户信息</li>
 * <li>用户查询：支持多种条件查询（域名、登录名、邮箱、手机号等）</li>
 * <li>缓存管理：自动维护用户详情缓存</li>
 * </ul>
 * </p>
 * <p>
 * 安全特性：
 * <ul>
 * <li>密码加密存储</li>
 * <li>第三方系统密码加密支持</li>
 * <li>登录成功生成Token令牌</li>
 * </ul>
 * </p>
 *
 * @author system
 * @version 1.0
 * @see UserEntity
 * @see UserDao
 */
@Service
public class UserService extends BaseService<UserEntity, String> {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao dao;

    @Autowired
    protected UserService(UserDao dao) {
        super(dao);
        this.dao = dao;
    }

    /**
     * 获取UserService实例
     * <p>
     * 通过Spring容器获取Service实例，用于在非Spring管理的类中调用服务
     * </p>
     *
     * @return UserService实例
     */
    public static UserService getInstance() {
        return SpringUtil.getBean(UserService.class);
    }

    /**
     * 搜索用户（分页）
     * <p>
     * 根据搜索参数进行分页查询，支持多种条件过滤
     * </p>
     *
     * @param param 搜索参数对象，包含分页信息和过滤条件
     * @return 分页查询结果集
     */
    public QueryResultSet<UserEntity> search(SearchParam param) {
        return dao.search(param);
    }

    /**
     * 根据参数查询用户列表
     * <p>
     * 根据搜索参数查询符合条件的用户列表，不分页
     * </p>
     *
     * @param param 搜索参数对象，包含过滤条件
     * @return 用户实体列表
     */
    public List<UserEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    /**
     * 更新用户信息
     * <p>
     * 更新用户基本信息，同时处理第三方密码加密（如果启用）。
     * 更新成功后会清除用户详情缓存。
     * </p>
     *
     * @param entity 用户实体对象，包含需要更新的用户信息
     */
    @Transactional
    public void update(UserEntity entity) {
        logger.debug("更新用户: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());

        String partyPwd = entity.getThirdPartyPwd();
        try {
            boolean thirdPartyEncryptPwdEnabled = ConfigHelper
                    .getBoolean(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_ENABLE_KEY, false);
            String thirdPartyEncryptPwd = ConfigHelper.getString(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_KEY, "szyh$123");
            if (thirdPartyEncryptPwdEnabled && StringUtils.isNotEmpty(partyPwd)) {
                byte[] encrypt = CryptUtil.encrypt(partyPwd.getBytes(), thirdPartyEncryptPwd);
                entity.setThirdPartyPwd(CryptUtil.encodeBase64(encrypt));
            }
        } catch (Exception e) {
            logger.error("第三方密码加密失败：", e);
        }

        dao.update(entity);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE,
                getSecurityUsername(entity.getUserDomain(), entity.getLoginName()));
        logger.debug("更新用户成功: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
    }

    private String getSecurityUsername(String userDomain, String loginName) {
        return userDomain + ":" + loginName;
    }

    /**
     * 重置用户密码
     * <p>
     * 管理员功能，强制重置指定用户的密码。
     * 新密码会进行加密存储，并清除用户详情缓存。
     * </p>
     *
     * @param userDomain 用户域
     * @param loginName 登录名
     * @param newPassword 新密码（明文）
     * @return 更新后的用户实体对象
     * @throws BaseRuntimeException 如果用户不存在
     */
    @Transactional
    public UserEntity resetPwd(String userDomain, String loginName, String newPassword) {
        UserEntity entity = findByUserDomainAndLoginName(userDomain, loginName);
        if (entity == null) {
            logger.warn("重置密码失败，用户不存在: userDomain={}, loginName={}", userDomain, loginName);
            throw new BaseRuntimeException(RbacConstants.MSG_USER_NOT_FOUND);
        }
        logger.info("重置用户密码: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        String newPwd = PasswordHelper.encryptPwd(entity.getUserDomain(), entity.getLoginName(), newPassword);
        entity.setPassword(newPwd);
        dao.update(entity);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE,
                getSecurityUsername(entity.getUserDomain(), entity.getLoginName()));
        logger.info("重置用户密码成功: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        return entity;
    }

    /**
     * 用户登录验证
     * <p>
     * 验证用户登录名和密码是否匹配。
     * 支持PC端和移动端两种登录方式：
     * <ul>
     * <li>移动端登录成功返回生成的Token令牌</li>
     * <li>PC端登录成功返回"success"字符串</li>
     * <li>登录失败返回null</li>
     * </ul>
     * </p>
     *
     * @param userDomain 用户域
     * @param loginName 登录名
     * @param password 登录密码（明文）
     * @return 登录成功返回Token（移动端）或"success"（PC端），失败返回null
     */
    public String authLogin(String userDomain, String loginName, String password) {

        UserEntity entity = findByUserDomainAndLoginName(userDomain, loginName);
        if (entity == null) {
            logger.warn("登录失败，用户不存在: userDomain={}, loginName={}", userDomain, loginName);
            return null;
        }
        logger.debug("用户登录验证: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        String newPwd = PasswordHelper.encryptPwd(userDomain, loginName, password);
        boolean isMobileRequest = ClientHelper.isMobileRequest(WebHelper.getHttpRequest());
        if (isMobileRequest) {
            if (StringUtils.equals(newPwd, entity.getPassword())) {
                String token = TokenGenerator.generateValue();
                logger.info("用户登录成功(REST): userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
                return token;
            }
        } else {
            if (StringUtils.equals(newPwd, entity.getPassword())) {
                logger.info("用户登录成功: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
                return "success";
            }
        }
        logger.warn("用户登录失败，密码不正确: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        return null;
    }

    /**
     * 修改用户密码
     * <p>
     * 用户自助修改密码功能，需要验证原密码是否正确。
     * 原密码验证通过后，更新为新密码并清除用户详情缓存。
     * </p>
     *
     * @param userDomain 用户域
     * @param loginName 登录名
     * @param oldPwd 原密码（明文）
     * @param newPassword 新密码（明文）
     * @return 更新后的用户实体对象
     * @throws BaseRuntimeException 如果用户不存在或原密码不正确
     */
    @Transactional
    public UserEntity changePwd(String userDomain, String loginName, String oldPwd, String newPassword) {
        UserEntity entity = findByUserDomainAndLoginName(userDomain, loginName);

        if (entity == null) {
            logger.warn("修改密码失败，用户不存在: userDomain={}, loginName={}", userDomain, loginName);
            throw new BaseRuntimeException(RbacConstants.MSG_USER_NOT_FOUND);
        }
        logger.info("修改用户密码: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        String encryptPwd = PasswordHelper.encryptPayPwd(userDomain, loginName, oldPwd);
        String newPwd = PasswordHelper.encryptPwd(entity.getUserDomain(), entity.getLoginName(), newPassword);
        if (StringUtils.equals(encryptPwd, entity.getPassword())) {
            entity.setPassword(newPwd);
            dao.update(entity);
            AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE,
                    getSecurityUsername(entity.getUserDomain(), entity.getLoginName()));
            logger.info("修改用户密码成功: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
            return entity;
        } else {
            logger.warn("修改密码失败，原密码不正确: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
            throw new BaseRuntimeException(RbacConstants.MSG_OLD_PASSWORD_INCORRECT);
        }
    }

    /**
     * 创建用户
     * <p>
     * 创建新用户，包含以下处理：
     * <ul>
     * <li>密码加密：对用户密码进行加密存储</li>
     * <li>默认密码：如果未设置密码，使用系统默认密码</li>
     * <li>第三方密码：如果启用第三方密码加密，对第三方密码进行加密</li>
     * <li>缓存清理：创建成功后清除用户详情缓存</li>
     * </ul>
     * </p>
     *
     * @param entity 用户实体对象，包含新用户信息
     */
    @Transactional
    public void create(UserEntity entity) {
        logger.info("创建用户: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        String password = entity.getPassword();
        if (StringUtils.isEmpty(password)) {
            entity.setPassword(ConfigHelper.getString(RbacConstants.USER_DEFAULT_PASSWORD));
        }
        String encryptPwd = PasswordHelper.encryptPwd(entity.getUserDomain(), entity.getLoginName(),
                entity.getPassword());
        entity.setPassword(encryptPwd);

        String partyPwd = entity.getThirdPartyPwd();
        try {
            boolean thirdPartyEncryptPwdEnabled = ConfigHelper
                    .getBoolean(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_ENABLE_KEY, false);
            String thirdPartyEncryptPwd = ConfigHelper.getString(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_KEY, "szyh$123");
            if (thirdPartyEncryptPwdEnabled && StringUtils.isNotEmpty(partyPwd)) {
                byte[] encrypt = CryptUtil.encrypt(partyPwd.getBytes(), thirdPartyEncryptPwd);
                entity.setThirdPartyPwd(CryptUtil.encodeBase64(encrypt));
            }
        } catch (Exception e) {
            logger.error("第三方密码加密失败：", e);
        }

        dao.create(entity);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE,
                getSecurityUsername(entity.getUserDomain(), entity.getLoginName()));
        logger.info("创建用户成功: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户实体对象，不存在返回null
     */
    public UserEntity findById(String id) {
        return dao.findById(id);
    }

    /**
     * 根据ID列表批量查询用户
     *
     * @param ids 用户ID列表
     * @return 用户列表
     */
    public List<UserEntity> findByIds(List<String> ids) {
        return dao.findByIds(ids);
    }

    /**
     * 根据用户域查询用户列表
     *
     * @param domain 用户域
     * @return 用户实体列表
     */
    public List<UserEntity> findByDomain(String domain) {
        return dao.findByDomain(domain);
    }

    /**
     * 根据用户域和登录名查询用户
     *
     * @param userDomain 用户域
     * @param loginName 登录名
     * @return 用户实体对象，不存在返回null
     */
    public UserEntity findByUserDomainAndLoginName(String userDomain, String loginName) {
        return dao.findByUserDomainAndLoginName(userDomain, loginName);
    }

    /**
     * 根据用户域和显示名查询用户
     *
     * @param domain 用户域
     * @param display 显示名
     * @return 用户实体对象，不存在返回null
     */
    public UserEntity findByDomainAndDisplay(String domain, String display) {
        return dao.findByDomainAndDisplay(domain, display);
    }

    /**
     * 根据外部用户ID查询用户
     *
     * @param extUserId 外部系统用户ID
     * @return 用户实体对象，不存在返回null
     */
    public UserEntity findByExtUserId(String extUserId) {
        return dao.findByExtUserId(extUserId);
    }

    /**
     * 根据用户域和邮箱查询用户
     *
     * @param domain 用户域
     * @param email 邮箱地址
     * @return 用户实体对象，不存在返回null
     */
    public UserEntity findByDomainAndEmail(String domain, String email) {
        return dao.findByDomainAndEmail(domain, email);
    }

    /**
     * 根据用户域和手机号查询用户
     *
     * @param domain 用户域
     * @param mobileNo 手机号
     * @return 用户实体对象，不存在返回null
     */
    public UserEntity findByDomainAndMobileNo(String domain, String mobileNo) {
        return dao.findByDomainAndMobileNo(domain, mobileNo);
    }

    /**
     * 根据用户域和登录名模糊查询用户列表
     *
     * @param userDomain 用户域
     * @param loginNameLike 登录名（支持模糊匹配）
     * @return 用户实体列表
     */
    public List<UserEntity> findByUserDomainAndLikeLoginName(String userDomain, String loginNameLike) {
        return dao.findByUserDomainAndLikeLoginName(userDomain, loginNameLike);
    }

    /**
     * 根据用户域和手机号模糊查询用户列表
     *
     * @param domain 用户域
     * @param mobileNoLike 手机号（支持模糊匹配）
     * @return 用户实体列表
     */
    public List<UserEntity> findByDomainAndLikeMobileNo(String domain, String mobileNoLike) {
        return dao.findByDomainAndLikeMobileNo(domain, mobileNoLike);
    }

    /**
     * 批量删除用户（优化：使用批量删除SQL提升性能）
     *
     * @param users 用户ID字符串，格式：userId,userId,userId
     */
    @Transactional
    public void deleteMany(String users) {
        logger.info("批量删除用户: users={}", users);
        if (StringUtils.isEmpty(users)) {
            logger.warn("批量删除用户失败，参数为空");
            return;
        }

        String[] userIdArray = StringUtils.split(users, ",");
        if (userIdArray.length > 0) {
            dao.deleteByIds(Arrays.asList(userIdArray));
            logger.info("批量删除用户成功: count={}", userIdArray.length);
        } else {
            logger.warn("批量删除用户失败，没有有效的用户ID");
        }
    }

    /**
     * 删除用户（根据用户域和登录名）
     *
     * @param userDomain 用户域
     * @param loginName 登录名
     */
    @Transactional
    public void delete(String userDomain, String loginName) {
        dao.delete(userDomain, loginName);
    }

    /**
     * 验证用户密码是否正确
     * <p>
     * 验证指定用户的登录密码是否匹配。
     * 常用于二次验证场景。
     * </p>
     *
     * @param userDomain 用户域
     * @param loginName 登录名
     * @param password 待验证的密码（明文）
     * @return 密码正确返回true，否则返回false
     */
    public boolean authenticate(String userDomain, String loginName, String password) {
        UserEntity userEntity = findByUserDomainAndLoginName(userDomain, loginName);
        if (userEntity == null) {
            logger.warn("验证密码失败，用户不存在: userDomain={}, loginName={}", userDomain, loginName);
            return false;
        }
        logger.debug("验证用户密码: userDomain={}, loginName={}", userEntity.getUserDomain(), userEntity.getLoginName());
        String encryptPwd = PasswordHelper.encryptPwd(userDomain, loginName, password);
        boolean result = StringUtils.equals(encryptPwd, userEntity.getPassword());
        logger.debug("验证用户密码结果: userDomain={}, loginName={}, result={}", userEntity.getUserDomain(),
                userEntity.getLoginName(), result);
        return result;
    }

    /**
     * 从Excel批量导入用户
     * <p>
     * 批量导入用户数据，包含以下处理：
     * <ul>
     * <li>数据验证：跳过无效数据（缺少必填字段）</li>
     * <li>新增用户：用户不存在时创建新用户</li>
     * <li>更新用户：用户已存在时更新用户信息</li>
     * <li>密码处理：使用系统默认密码或导入的密码</li>
     * <li>错误处理：记录失败数据，继续处理其他数据</li>
     * </ul>
     * </p>
     *
     * @param importList 用户导入VO列表
     */
    @Transactional
    public void importExcel(List<UserImportVo> importList) {
        logger.info("开始导入用户: count={}", importList.size());
        int successCount = 0;
        int skipCount = 0;
        int updateCount = 0;

        for (UserImportVo importVo : importList) {
            if (StringUtils.isEmpty(importVo.getUserDomain()) || StringUtils.isEmpty(importVo.getLoginName())) {
                skipCount++;
                logger.warn("跳过无效用户数据: userDomain={}, loginName={}", importVo.getUserDomain(), importVo.getLoginName());
                continue;
            }

            try {
                UserEntity existingUser = findByUserDomainAndLoginName(importVo.getUserDomain(),
                        importVo.getLoginName());
                if (existingUser == null) {
                    UserEntity user = buildUserFromImportVo(importVo);
                    create(user);
                    successCount++;
                    logger.debug("导入新用户成功: userDomain={}, loginName={}", importVo.getUserDomain(),
                            importVo.getLoginName());
                } else {
                    updateUserFromImportVo(existingUser, importVo);
                    update(existingUser);
                    updateCount++;
                    logger.debug("更新用户成功: userDomain={}, loginName={}", importVo.getUserDomain(),
                            importVo.getLoginName());
                }
            } catch (Exception e) {
                logger.error("导入用户失败: userDomain={}, loginName={}", importVo.getUserDomain(), importVo.getLoginName(),
                        e);
                skipCount++;
            }
        }

        logger.info("导入用户完成: 总数={}, 成功={}, 更新={}, 跳过={}",
                importList.size(), successCount, updateCount, skipCount);
    }

    /**
     * 从导入VO构建用户实体
     * <p>
     * 将用户导入VO转换为用户实体对象，设置默认值。
     * </p>
     *
     * @param importVo 用户导入VO对象
     * @return 用户实体对象
     */
    private UserEntity buildUserFromImportVo(UserImportVo importVo) {
        UserEntity user = new UserEntity();

        // We do not need to set ID here if it is auto-generated by DB or framework?
        // UserEntity.id is AutoGeneratedKey.
        // But previously we customized ID?
        // IdGenerator.getInstance().nextStringId() was used.
        // We should set ID explicitly if we want to control it, or let DB handle it.
        // The old code set userId to nextStringId().
        // We will set ID to nextStringId() as well for consistency with framework
        // typical usage.
        user.setId(IdGenerator.getInstance().nextStringId());

        user.setUserDomain(importVo.getUserDomain());
        user.setLoginName(importVo.getLoginName());
        user.setRealName(importVo.getRealName());
        user.setDisplay(StringUtils.isNotEmpty(importVo.getDisplay()) ? importVo.getDisplay() : importVo.getRealName());
        user.setEmail(importVo.getEmail());
        user.setMobileNo(importVo.getMobileNo());
        user.setStatus(
                StringUtils.isNotEmpty(importVo.getStatus()) ? importVo.getStatus() : UserStatus.ACTIVE.getCode());
        user.setSex(importVo.getSex());
        user.setDescription(importVo.getDescription());
        user.setDeptNo(importVo.getDeptNo());
        user.setJobNo(importVo.getJobNo());
        user.setAddress(importVo.getAddress());
        user.setBirthday(importVo.getBirthday());
        user.setIdNo(importVo.getIdNo());

        if (StringUtils.isNotEmpty(importVo.getPassword())) {
            user.setPassword(importVo.getPassword());
        } else {
            user.setPassword(ConfigHelper.getString(RbacConstants.USER_DEFAULT_PASSWORD));
        }
        return user;
    }

    /**
     * 从导入VO更新用户实体
     * <p>
     * 根据导入VO中的数据更新已存在的用户实体。
     * 仅更新非空字段。
     * </p>
     *
     * @param existingUser 已存在的用户实体对象
     * @param importVo 用户导入VO对象
     */
    private void updateUserFromImportVo(UserEntity existingUser, UserImportVo importVo) {
        if (StringUtils.isNotEmpty(importVo.getRealName())) {
            existingUser.setRealName(importVo.getRealName());
        }
        if (StringUtils.isNotEmpty(importVo.getDisplay())) {
            existingUser.setDisplay(importVo.getDisplay());
        }
        if (StringUtils.isNotEmpty(importVo.getEmail())) {
            existingUser.setEmail(importVo.getEmail());
        }
        if (StringUtils.isNotEmpty(importVo.getMobileNo())) {
            existingUser.setMobileNo(importVo.getMobileNo());
        }
        if (StringUtils.isNotEmpty(importVo.getStatus())) {
            existingUser.setStatus(importVo.getStatus());
        }
        if (StringUtils.isNotEmpty(importVo.getDescription())) {
            existingUser.setDescription(importVo.getDescription());
        }
        if (StringUtils.isNotEmpty(importVo.getSex())) {
            existingUser.setSex(importVo.getSex());
        }
        if (StringUtils.isNotEmpty(importVo.getDeptNo())) {
            existingUser.setDeptNo(importVo.getDeptNo());
        }
        if (StringUtils.isNotEmpty(importVo.getJobNo())) {
            existingUser.setJobNo(importVo.getJobNo());
        }
        if (StringUtils.isNotEmpty(importVo.getAddress())) {
            existingUser.setAddress(importVo.getAddress());
        }
        if (importVo.getBirthday() != null) {
            existingUser.setBirthday(importVo.getBirthday());
        }
        if (StringUtils.isNotEmpty(importVo.getIdNo())) {
            existingUser.setIdNo(importVo.getIdNo());
        }
        if (StringUtils.isNotEmpty(importVo.getPassword())) {
            String encryptPwd = PasswordHelper.encryptPwd(
                    existingUser.getUserDomain(), existingUser.getLoginName(), importVo.getPassword());
            existingUser.setPassword(encryptPwd);
        }
    }

    /**
     * 导出用户数据到Excel
     * <p>
     * 将用户列表导出为Excel文件，通过HttpServletResponse响应输出。
     * 文件名包含时间戳，格式为：用户数据_yyyyMMddHHmmss.xls
     * </p>
     *
     * @param response HTTP响应对象
     * @param list 用户实体列表
     * @throws Exception 导出过程中的异常
     */
    public void exportExcel(HttpServletResponse response, List<UserEntity> list) throws Exception {
        logger.info("导出用户Excel: count={}", list.size());
        Workbook book = generateWorkbook(list);

        response.reset();
        response.setContentType("application/x-msdownload");
        String fileName = RbacConstants.MSG_EXPORT_FILE_NAME;
        fileName = fileName + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        response.setHeader("Content-disposition",
                "attachment; filename=" + new String(fileName.getBytes("gb2312"), "ISO-8859-1") + ".xls");

        try (ServletOutputStream outStream = response.getOutputStream()) {
            book.write(outStream);
        } finally {
            book.close();
        }
        logger.info("导出用户Excel成功: count={}", list.size());
    }

    /**
     * 生成Excel工作簿
     * <p>
     * 将用户实体列表转换为Excel工作簿对象。
     * </p>
     *
     * @param list 用户实体列表
     * @return Excel工作簿对象
     */
    private Workbook generateWorkbook(List<UserEntity> list) {
        List<UserExportVo> exportList = convertToExportVo(list);
        ExportParams params = new ExportParams();
        params.setSheetName("用户数据");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("title", params);
        dataMap.put("entity", UserExportVo.class);
        dataMap.put("data", exportList);
        List<Map<String, Object>> sheetsList = new ArrayList<>();
        sheetsList.add(dataMap);
        return ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
    }

    /**
     * 转换为导出VO列表
     * <p>
     * 将用户实体列表转换为导出VO列表，用于Excel导出。
     * </p>
     *
     * @param list 用户实体列表
     * @return 用户导出VO列表
     */
    private List<UserExportVo> convertToExportVo(List<UserEntity> list) {
        List<UserExportVo> exportList = new ArrayList<>();
        for (UserEntity user : list) {
            UserExportVo vo = new UserExportVo();
            vo.setUserDomain(user.getUserDomain());
            vo.setLoginName(user.getLoginName());
            vo.setRealName(user.getRealName());
            vo.setDisplay(user.getDisplay());
            vo.setMobileNo(user.getMobileNo());
            vo.setEmail(user.getEmail());
            vo.setStatus(user.getStatus());
            vo.setSex(user.getSex());
            vo.setDescription(user.getDescription());
            vo.setDeptNo(user.getDeptNo());
            vo.setJobNo(user.getJobNo());
            vo.setAddress(user.getAddress());
            vo.setBirthday(user.getBirthday());
            vo.setIdNo(user.getIdNo());
            vo.setLatestLoginTime(user.getLatestLoginTime());
            vo.setCreatedAt(user.getCreatedAt());
            exportList.add(vo);
        }
        return exportList;
    }
}
