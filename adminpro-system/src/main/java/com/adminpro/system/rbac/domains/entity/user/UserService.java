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

    public static UserService getInstance() {
        return SpringUtil.getBean(UserService.class);
    }

    public QueryResultSet<UserEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<UserEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

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
                new UserIden(entity.getUserDomain(), entity.getLoginName()).toSecurityUsername());
        logger.debug("更新用户成功: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
    }

    @Transactional
    public UserEntity resetPwd(UserIden userIden, String newPassword) {
        UserEntity entity = findByIden(userIden);
        if (entity == null) {
            logger.warn("重置密码失败，用户不存在: userDomain={}, loginName={}", userIden.getUserDomain(), userIden.getLoginName());
            throw new BaseRuntimeException(RbacConstants.MSG_USER_NOT_FOUND);
        }
        logger.info("重置用户密码: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        String newPwd = PasswordHelper.encryptPwd(new UserIden(entity.getUserDomain(), entity.getLoginName()),
                newPassword);
        entity.setPassword(newPwd);
        dao.update(entity);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE,
                new UserIden(entity.getUserDomain(), entity.getLoginName()).toSecurityUsername());
        logger.info("重置用户密码成功: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        return entity;
    }

    public String authLogin(UserIden userIden, String password) {

        UserEntity entity = findByIden(userIden);
        if (entity == null) {
            logger.warn("登录失败，用户不存在: userDomain={}, loginName={}", userIden.getUserDomain(), userIden.getLoginName());
            return null;
        }
        logger.debug("用户登录验证: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        String newPwd = PasswordHelper.encryptPwd(userIden, password);
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

    @Transactional
    public UserEntity changePwd(UserIden userIden, String oldPwd, String newPassword) {
        UserEntity entity = findByIden(userIden);

        if (entity == null) {
            logger.warn("修改密码失败，用户不存在: userDomain={}, loginName={}", userIden.getUserDomain(), userIden.getLoginName());
            throw new BaseRuntimeException(RbacConstants.MSG_USER_NOT_FOUND);
        }
        logger.info("修改用户密码: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        String encryptPwd = PasswordHelper.encryptPayPwd(userIden, oldPwd);
        String newPwd = PasswordHelper.encryptPwd(new UserIden(entity.getUserDomain(), entity.getLoginName()),
                newPassword);
        if (StringUtils.equals(encryptPwd, entity.getPassword())) {
            entity.setPassword(newPwd);
            dao.update(entity);
            AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE,
                    new UserIden(entity.getUserDomain(), entity.getLoginName()).toSecurityUsername());
            logger.info("修改用户密码成功: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
            return entity;
        } else {
            logger.warn("修改密码失败，原密码不正确: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
            throw new BaseRuntimeException(RbacConstants.MSG_OLD_PASSWORD_INCORRECT);
        }
    }

    @Transactional
    public void create(UserEntity entity) {
        logger.info("创建用户: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
        String password = entity.getPassword();
        if (StringUtils.isEmpty(password)) {
            entity.setPassword(ConfigHelper.getString(RbacConstants.USER_DEFAULT_PASSWORD));
        }
        String encryptPwd = PasswordHelper.encryptPwd(new UserIden(entity.getUserDomain(), entity.getLoginName()),
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
                new UserIden(entity.getUserDomain(), entity.getLoginName()).toSecurityUsername());
        logger.info("创建用户成功: userDomain={}, loginName={}", entity.getUserDomain(), entity.getLoginName());
    }

    public UserEntity findByIden(UserIden userIden) {
        return dao.findByIden(userIden);
    }

    public UserEntity findById(String id) {
        return dao.findById(id);
    }

    public List<UserEntity> findByDomain(String domain) {
        return dao.findByDomain(domain);
    }

    public UserEntity findByUserDomainAndUserId(String userDomain, String userId) {
        UserEntity user = dao.findById(userId);
        if (user != null && StringUtils.equals(user.getUserDomain(), userDomain)) {
            return user;
        }
        return null;
    }

    public UserEntity findByUserDomainAndLoginName(String userDomain, String loginName) {
        return dao.findByUserDomainAndLoginName(userDomain, loginName);
    }

    public UserEntity findByDomainAndDisplay(String domain, String display) {
        return dao.findByDomainAndDisplay(domain, display);
    }

    public UserEntity findByExtUserId(String extUserId) {
        return dao.findByExtUserId(extUserId);
    }

    public UserEntity findByDomainAndEmail(String domain, String email) {
        return dao.findByDomainAndEmail(domain, email);
    }

    public UserEntity findByDomainAndMobileNo(String domain, String mobileNo) {
        return dao.findByDomainAndMobileNo(domain, mobileNo);
    }

    public List<UserEntity> findByUserDomainAndLikeLoginName(String userDomain, String loginNameLike) {
        return dao.findByUserDomainAndLikeLoginName(userDomain, loginNameLike);
    }

    public List<UserEntity> findByDomainAndLikeMobileNo(String domain, String mobileNoLike) {
        return dao.findByDomainAndLikeMobileNo(domain, mobileNoLike);
    }

    /**
     * 批量删除用户（优化：使用批量删除SQL提升性能）
     *
     * @param users 用户ID字符串，格式：userDomain_userId,userDomain_userId.
     *              Note: In new single ID strategy, we should ideally use ids.
     *              Compatibility: If we receive domain_id, we try to extract id.
     */
    @Transactional
    public void deleteMany(String users) {
        logger.info("批量删除用户: users={}", users);
        if (StringUtils.isEmpty(users)) {
            logger.warn("批量删除用户失败，参数为空");
            return;
        }

        String[] userDomainIdArray = StringUtils.split(users, ",");
        List<String> ids = new ArrayList<>();

        for (String userDomainId : userDomainIdArray) {
            String[] split = userDomainId.split("_");
            if (split.length == 2) {
                // Assuming split[1] is the ID in the new system context
                ids.add(split[1]);
            } else {
                logger.warn("删除用户失败，格式不正确: userDomainId={}", userDomainId);
            }
        }

        if (!ids.isEmpty()) {
            dao.deleteByIds(ids);
            logger.info("批量删除用户成功: count={}", ids.size());
        } else {
            logger.warn("批量删除用户失败，没有有效的用户ID");
        }
    }

    @Transactional
    public void delete(UserIden userIden) {
        dao.delete(userIden);
    }

    /**
     * 验证密码是否正确
     *
     * @param userIden
     * @param password
     * @return
     */
    public boolean authenticate(UserIden userIden, String password) {
        UserEntity userEntity = findByIden(userIden);
        if (userEntity == null) {
            logger.warn("验证密码失败，用户不存在: userDomain={}, loginName={}", userIden.getUserDomain(), userIden.getLoginName());
            return false;
        }
        logger.debug("验证用户密码: userDomain={}, loginName={}", userEntity.getUserDomain(), userEntity.getLoginName());
        String encryptPwd = PasswordHelper.encryptPwd(userIden, password);
        boolean result = StringUtils.equals(encryptPwd, userEntity.getPassword());
        logger.debug("验证用户密码结果: userDomain={}, loginName={}, result={}", userEntity.getUserDomain(),
                userEntity.getLoginName(), result);
        return result;
    }

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
     */
    private UserEntity buildUserFromImportVo(UserImportVo importVo) {
        UserEntity user = new UserEntity();
        // user.setUserIden(...) is removed.
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
                    new UserIden(existingUser.getUserDomain(), existingUser.getLoginName()), importVo.getPassword());
            existingUser.setPassword(encryptPwd);
        }
    }

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
