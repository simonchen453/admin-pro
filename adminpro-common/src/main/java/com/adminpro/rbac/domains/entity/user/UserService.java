package com.adminpro.rbac.domains.entity.user;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.cache.AppCache;
import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.framework.security.auth.TokenGenerator;
import com.adminpro.rbac.api.PasswordHelper;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.rbac.common.RbacConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService extends BaseService<UserEntity, UserIden> {

    @Autowired
    protected UserService(UserDao dao) {
        super(dao);
    }

    public static UserService getInstance() {
        return SpringUtil.getBean(UserService.class);
    }

    @Autowired
    private UserDao dao;

    public QueryResultSet<UserEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<UserEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    @Transactional
    public void update(UserEntity entity) {
        dao.update(entity);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, entity.getUserIden().toSecurityUsername());
    }

    @Transactional
    public UserEntity resetPwd(UserIden userIden, String newPassword) {
        UserEntity entity = UserService.getInstance().findById(userIden);
        String newPwd = PasswordHelper.encryptPwd(entity.getUserIden(), newPassword);
        entity.setPassword(newPwd);
        dao.update(entity);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, entity.getUserIden().toSecurityUsername());
        return entity;
    }

    public String authLogin(UserIden userIden, String password) {
        UserEntity entity = UserService.getInstance().findById(userIden);
        String newPwd = PasswordHelper.encryptPwd(userIden, password);
        boolean restRequest = WebHelper.isRestRequest();
        if (restRequest) {
            if (StringUtils.equals(newPwd, entity.getPassword())) {
                String token = TokenGenerator.generateValue();
                return token;
            }
        } else {
            if (StringUtils.equals(newPwd, entity.getPassword())) {
                return "success";
            }
        }
        return null;
    }

    @Transactional
    public UserEntity changePwd(UserIden userIden, String oldPwd, String newPassword) {
        UserEntity entity = UserService.getInstance().findById(userIden);

        if (entity == null) {
            throw new BaseRuntimeException("用户不存在");
        }

        String encryptPwd = PasswordHelper.encryptPayPwd(userIden, oldPwd);
        String newPwd = PasswordHelper.encryptPwd(entity.getUserIden(), newPassword);
        if (StringUtils.equals(encryptPwd, entity.getPassword())) {
            entity.setPassword(newPwd);
            dao.update(entity);
            AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, entity.getUserIden().toSecurityUsername());
            return entity;
        } else {
            throw new BaseRuntimeException("原密码不正确");
        }
    }

    @Transactional
    public void create(UserEntity entity) {
        String password = entity.getPassword();
        if (StringUtils.isEmpty(password)) {
            entity.setPassword(ConfigHelper.getString(RbacConstants.USER_DEFAULT_PASSWORD));
        }
        String encryptPwd = PasswordHelper.encryptPwd(entity.getUserIden(), entity.getPassword());

        entity.setPassword(encryptPwd);
        dao.create(entity);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, entity.getUserIden().toSecurityUsername());
    }

    public UserEntity findById(UserIden userIden) {
        return dao.findById(userIden);
    }

    public List<UserEntity> findByDomain(String domain) {
        return dao.findByDomain(domain);
    }

    public UserEntity findByUserDomainAndUserId(String userDomain, String userId) {
        return dao.findById(new UserIden(userDomain, userId));
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

    public List<UserEntity> findByDomainAndLikeUserId(String domain, String userIdLike) {
        return dao.findByDomainAndLikeUserId(domain, userIdLike);
    }

    /**
     * @param users
     */
    @Transactional
    public void deleteMany(String users) {
        String[] userDomainIdArray = StringUtils.split(users, ",");
        for (String userDomainId : userDomainIdArray) {
            String[] split = userDomainId.split("_");
            UserIden userIden = new UserIden(split[0], split[1]);
            dao.delete(userIden);
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
        UserEntity userEntity = UserService.getInstance().findById(userIden);
        String encryptPwd = PasswordHelper.encryptPwd(userIden, password);
        return StringUtils.equals(encryptPwd, userEntity.getPassword());
    }
}
