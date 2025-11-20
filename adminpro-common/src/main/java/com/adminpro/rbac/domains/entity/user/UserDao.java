package com.adminpro.rbac.domains.entity.user;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.base.util.CryptUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.core.jdbc.sqlbuilder.InsertBuilder;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.core.jdbc.sqlbuilder.UpdateBuilder;
import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.web.BaseConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 用户表 数据库持久层
 *
 * @author simon
 * @date 2018-09-06
 */
@Component
public class UserDao extends BaseDao<UserEntity, UserIden> {

    private static final String SQL_USER_LIST = "select u.* from sys_user_tbl u left join sys_dept_tbl d on u.col_dept_no = d.col_no";

    public QueryResultSet<UserEntity> search(SearchParam param) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setQuery(SQL_USER_LIST);
        select.setSearchParam(param);
        prepareSelectBuilder(select, param);
        return search(select);
    }

    public List<UserEntity> findByParam(SearchParam param) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setQuery(SQL_USER_LIST);
        prepareSelectBuilder(select, param);
        return execute(select);
    }

    private void prepareSelectBuilder(SelectBuilder select, SearchParam param) {
        Map<String, Object> filters = param.getFilters();
        String status = (String) filters.get("status");
        String loginName = (String) filters.get("loginName");
        String realName = (String) filters.get("realName");
        String userDomain = (String) filters.get("userDomain");
        String deptId = (String) filters.get("deptId");
        if (StringUtils.isNotEmpty(status)) {
            select.addWhereAnd("u." + UserEntity.COL_STATUS + EQ, status);
        }
        if (StringUtils.isNotEmpty(loginName)) {
            select.addWhereAnd("u." + UserEntity.COL_LOGIN_NAME + LIKE, PERCENT + loginName + PERCENT);
        }
        if (StringUtils.isNotEmpty(realName)) {
            select.addWhereAnd("u." + UserEntity.COL_REAL_NAME + LIKE, PERCENT + realName + PERCENT);
        }
        if (StringUtils.isNotEmpty(userDomain)) {
            select.addWhereAnd("u." + UserEntity.COL_USER_DOMAIN + EQ, userDomain);
        }
        if (StringUtils.isNotEmpty(deptId)) {
            select.addWhereAnd("(d." + DeptEntity.COL_ID + " = ? or d.col_id in (select t.col_id from sys_dept_tbl t where find_in_set (?, col_ancestors)))", deptId, deptId);
        }
    }

    /**
     * 创建 UserEntity
     *
     * @param entity
     */
    @Override
    public void create(UserEntity entity) {
        InsertBuilder insert = new InsertBuilder(UserEntity.TABLE_NAME);
        handleAuditColumnValues(insert, entity);
        insert.addColumnValue(UserEntity.COL_USER_DOMAIN, entity.getUserDomain());
        insert.addColumnValue(UserEntity.COL_USER_ID, entity.getUserId());
        insert.addColumnValue(UserEntity.COL_LOGIN_NAME, entity.getLoginName());
        insert.addColumnValue(UserEntity.COL_DISPLAY, entity.getDisplay());
        insert.addColumnValue(UserEntity.COL_REAL_NAME, entity.getRealName());
        insert.addColumnValue(UserEntity.COL_ID_NO, entity.getIdNo());
        insert.addColumnValue(UserEntity.COL_EMAIL, entity.getEmail());
        insert.addColumnValue(UserEntity.COL_STATUS, entity.getStatus());
        insert.addColumnValue(UserEntity.COL_AUTHENTICATED, entity.isAuthenticated());
        insert.addColumnValue(UserEntity.COL_MOBILE_NO, entity.getMobileNo());
        insert.addColumnValue(UserEntity.COL_IS_SYSTEM, entity.isSystem());
        insert.addColumnValue(UserEntity.COL_ADDRESS, entity.getAddress());
        insert.addColumnValue(UserEntity.COL_PWD, entity.getPassword());
        insert.addColumnValue(UserEntity.COL_BIRTHDAY, entity.getBirthday());
        insert.addColumnValue(UserEntity.COL_SEX, entity.getSex());
        insert.addColumnValue(UserEntity.COL_MARITAL, entity.getMarital());
        insert.addColumnValue(UserEntity.COL_NATION, entity.getNation());
        insert.addColumnValue(UserEntity.COL_PAY_NO_PWD, entity.getPayNoPwd());
        insert.addColumnValue(UserEntity.COL_PAY_PWD, entity.getPayPwd());
        insert.addColumnValue(UserEntity.COL_PAY_NO_PWD_POINT, entity.getPayNoPwdPoint());
        insert.addColumnValue(UserEntity.COL_AVATAR_URL, entity.getAvatarUrl());
        insert.addColumnValue(UserEntity.COL_POST, entity.getPost());
        insert.addColumnValue(UserEntity.COL_JOB_NO, entity.getJobNo());
        insert.addColumnValue(UserEntity.COL_EXT_USER_ID, entity.getExtUserId());
        insert.addColumnValue(UserEntity.COL_AUTHENTICATE_DATE, entity.getAuthenticateDate());
        insert.addColumnValue(UserEntity.COL_LATEST_LOGIN_TIME, entity.getLatestLoginTime());
        insert.addColumnValue(UserEntity.COL_LATEST_CHANGE_PWD_TIME, entity.getLatestChangePwdTime());
        insert.addColumnValue(UserEntity.COL_DESCRIPTION, entity.getDescription());
        insert.addColumnValue(UserEntity.COL_DEPT_NO, entity.getDeptNo());
        insert.addColumnValue(UserEntity.COL_SHARE_CODE, entity.getShareCode());
        insert.addColumnValue(UserEntity.COL_PARENT_SHARE_CODE, entity.getParentShareCode());
        insert.addColumnValue(UserEntity.COL_INTEGRAL, entity.getIntegral());
        insert.addColumnValue(UserEntity.COL_PROVINCE, entity.getProvince());
        insert.addColumnValue(UserEntity.COL_CITY, entity.getCity());
        insert.addColumnValue(UserEntity.COL_PROFESSION, entity.getProfession());
        insert.addColumnValue(UserEntity.COL_DISTRICT, entity.getDistrict());
        insert.addColumnValue(UserEntity.COL_CONSTELLATION, entity.getConstellation());
        insert.addColumnValue(UserEntity.COL_THIRD_PARTY_USER_NAME, entity.getThirdPartyUserName());
        String partyPwd = entity.getThirdPartyPwd();

        try {
            boolean encryptPwdEnabled = ConfigHelper.getBoolean(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_ENABLE_KEY, false);
            String encryptPwd = ConfigHelper.getString(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_KEY, "szyh$123");
            if (encryptPwdEnabled && StringUtils.isNotEmpty(partyPwd)) {
                byte[] encrypt = CryptUtil.encrypt(partyPwd.getBytes(), encryptPwd);
                insert.addColumnValue(UserEntity.COL_THIRD_PARTY_PWD, CryptUtil.encodeBase64(encrypt));
            } else {
                insert.addColumnValue(UserEntity.COL_THIRD_PARTY_PWD, partyPwd);
            }
        } catch (Exception e) {
            logger.error("第三方密码加密失败：", e);
        }

        execute(insert);
    }

    /**
     * 更新 UserEntity
     *
     * @param entity
     */
    @Override
    public void update(UserEntity entity) {
        UpdateBuilder update = new UpdateBuilder(UserEntity.TABLE_NAME);
        handleAuditColumnValues(update, entity);
        update.addColumnValue(UserEntity.COL_USER_DOMAIN, entity.getUserDomain());
        update.addColumnValue(UserEntity.COL_USER_ID, entity.getUserId());
        update.addColumnValue(UserEntity.COL_LOGIN_NAME, entity.getLoginName());
        update.addColumnValue(UserEntity.COL_DISPLAY, entity.getDisplay());
        update.addColumnValue(UserEntity.COL_REAL_NAME, entity.getRealName());
        update.addColumnValue(UserEntity.COL_ID_NO, entity.getIdNo());
        update.addColumnValue(UserEntity.COL_EMAIL, entity.getEmail());
        update.addColumnValue(UserEntity.COL_STATUS, entity.getStatus());
        update.addColumnValue(UserEntity.COL_AUTHENTICATED, entity.isAuthenticated());
        update.addColumnValue(UserEntity.COL_MOBILE_NO, entity.getMobileNo());
        update.addColumnValue(UserEntity.COL_IS_SYSTEM, entity.isSystem());
        update.addColumnValue(UserEntity.COL_ADDRESS, entity.getAddress());
        update.addColumnValue(UserEntity.COL_PWD, entity.getPassword());
        update.addColumnValue(UserEntity.COL_BIRTHDAY, entity.getBirthday());
        update.addColumnValue(UserEntity.COL_SEX, entity.getSex());
        update.addColumnValue(UserEntity.COL_MARITAL, entity.getMarital());
        update.addColumnValue(UserEntity.COL_NATION, entity.getNation());
        update.addColumnValue(UserEntity.COL_PAY_NO_PWD, entity.getPayNoPwd());
        update.addColumnValue(UserEntity.COL_PAY_PWD, entity.getPayPwd());
        update.addColumnValue(UserEntity.COL_PAY_NO_PWD_POINT, entity.getPayNoPwdPoint());
        update.addColumnValue(UserEntity.COL_AVATAR_URL, entity.getAvatarUrl());
        update.addColumnValue(UserEntity.COL_POST, entity.getPost());
        update.addColumnValue(UserEntity.COL_JOB_NO, entity.getJobNo());
        update.addColumnValue(UserEntity.COL_EXT_USER_ID, entity.getExtUserId());
        update.addColumnValue(UserEntity.COL_AUTHENTICATE_DATE, entity.getAuthenticateDate());
        update.addColumnValue(UserEntity.COL_LATEST_LOGIN_TIME, entity.getLatestLoginTime());
        update.addColumnValue(UserEntity.COL_LATEST_CHANGE_PWD_TIME, entity.getLatestChangePwdTime());
        update.addColumnValue(UserEntity.COL_DESCRIPTION, entity.getDescription());
        update.addColumnValue(UserEntity.COL_DEPT_NO, entity.getDeptNo());
        update.addColumnValue(UserEntity.COL_SHARE_CODE, entity.getShareCode());
        update.addColumnValue(UserEntity.COL_PARENT_SHARE_CODE, entity.getParentShareCode());
        update.addColumnValue(UserEntity.COL_INTEGRAL, entity.getIntegral());
        update.addColumnValue(UserEntity.COL_PROVINCE, entity.getProvince());
        update.addColumnValue(UserEntity.COL_CITY, entity.getCity());
        update.addColumnValue(UserEntity.COL_DISTRICT, entity.getDistrict());
        update.addColumnValue(UserEntity.COL_PROFESSION, entity.getProfession());
        update.addColumnValue(UserEntity.COL_CONSTELLATION, entity.getConstellation());
        update.addColumnValue(UserEntity.COL_THIRD_PARTY_USER_NAME, entity.getThirdPartyUserName());
        update.addWhereAnd(UserEntity.COL_USER_DOMAIN + " = ?", entity.getUserDomain());
        update.addWhereAnd(UserEntity.COL_USER_ID + " = ?", entity.getUserId());

        String partyPwd = entity.getThirdPartyPwd();
        try {
            boolean encryptPwdEnabled = ConfigHelper.getBoolean(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_ENABLE_KEY, false);
            String encryptPwd = ConfigHelper.getString(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_KEY, "szyh$123");
            if (encryptPwdEnabled && StringUtils.isNotEmpty(partyPwd)) {
                byte[] encrypt = CryptUtil.encrypt(partyPwd.getBytes(), encryptPwd);
                update.addColumnValue(UserEntity.COL_THIRD_PARTY_PWD, CryptUtil.encodeBase64(encrypt));
            } else {
                update.addColumnValue(UserEntity.COL_THIRD_PARTY_PWD, partyPwd);
            }
        } catch (Exception e) {
            logger.error("第三方密码加密失败：", e);
        }

        execute(update);
    }

    /**
     * 根据userIden查找UserEntity对象
     *
     * @param userIden
     * @return
     */
    @Override
    public UserEntity findById(UserIden userIden) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, userIden.getUserDomain());
        select.addWhereAnd(UserEntity.COL_USER_ID + EQ, userIden.getUserId());
        return executeSingle(select);
    }

    public List<UserEntity> findByDomain(String domain) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, domain);
        return execute(select);
    }

    /**
     * 根据extUserId查找UserEntity对象
     *
     * @param extUserId
     * @return
     */
    public UserEntity findByExtUserId(String extUserId) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_EXT_USER_ID + EQ, extUserId);
        return executeSingle(select);
    }

    public UserEntity findByDomainAndEmail(String domain, String email) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, domain);
        select.addWhereAnd(UserEntity.COL_EMAIL + EQ, email);
        return executeSingle(select);
    }

    public UserEntity findByDomainAndDisplay(String domain, String display) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, domain);
        select.addWhereAnd(UserEntity.COL_DISPLAY + EQ, display);
        return executeSingle(select);
    }

    public UserEntity findByUserDomainAndLoginName(String userDomain, String loginName) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, userDomain);
        select.addWhereAnd(UserEntity.COL_LOGIN_NAME + EQ, loginName);
        return executeSingle(select);
    }

    public List<UserEntity> findByUserDomainAndLikeLoginName(String userDomain, String loginNameLike) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, userDomain);
        select.addWhereAnd(UserEntity.COL_LOGIN_NAME + LIKE, loginNameLike);
        return execute(select);
    }

    public UserEntity findByDomainAndMobileNo(String domain, String mobileNo) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, domain);
        select.addWhereAnd(UserEntity.COL_MOBILE_NO + EQ, mobileNo);
        return executeSingle(select);
    }

    public List<UserEntity> findByDomainAndLikeMobileNo(String domain, String mobileNoLike) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, domain);
        select.addWhereAnd(UserEntity.COL_MOBILE_NO + LIKE, mobileNoLike);
        return execute(select);
    }

    public List<UserEntity> findByDomainAndLikeUserId(String domain, String userIdLike) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, domain);
        select.addWhereAnd(UserEntity.COL_USER_ID + LIKE, userIdLike);
        return execute(select);
    }

    /**
     * 删除UserEntity
     *
     * @param userIden
     * @return
     */
    @Override
    public void delete(UserIden userIden) {
        DeleteBuilder delete = new DeleteBuilder(UserEntity.TABLE_NAME);
        delete.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, userIden.getUserDomain());
        delete.addWhereAnd(UserEntity.COL_USER_ID + EQ, userIden.getUserId());
        execute(delete);
    }

    /**
     * UserEntity表映射关系
     *
     * @return
     */
    protected RowMapper<UserEntity> getUserRowMapper() {
        return new RowMapper<UserEntity>() {
            @Override
            public UserEntity mapRow(ResultSet resultSet, int i) throws SQLException {
                UserEntity entity = new UserEntity();

                String userDomain = resultSet.getString(UserEntity.COL_USER_DOMAIN);
                String userId = resultSet.getString(UserEntity.COL_USER_ID);
                entity.setUserDomain(userDomain);
                entity.setUserId(userId);
                entity.setUserIden(new UserIden(userDomain, userId));
                entity.setLoginName(resultSet.getString(UserEntity.COL_LOGIN_NAME));
                entity.setDisplay(resultSet.getString(UserEntity.COL_DISPLAY));
                entity.setRealName(resultSet.getString(UserEntity.COL_REAL_NAME));
                entity.setIdNo(resultSet.getString(UserEntity.COL_ID_NO));
                entity.setEmail(resultSet.getString(UserEntity.COL_EMAIL));
                entity.setStatus(resultSet.getString(UserEntity.COL_STATUS));
                entity.setAuthenticated(resultSet.getBoolean(UserEntity.COL_AUTHENTICATED));
                entity.setMobileNo(resultSet.getString(UserEntity.COL_MOBILE_NO));
                entity.setSystem(resultSet.getBoolean(UserEntity.COL_IS_SYSTEM));
                entity.setAddress(resultSet.getString(UserEntity.COL_ADDRESS));
                entity.setPassword(resultSet.getString(UserEntity.COL_PWD));
                entity.setBirthday(resultSet.getDate(UserEntity.COL_BIRTHDAY));
                entity.setSex(resultSet.getString(UserEntity.COL_SEX));
                entity.setMarital(resultSet.getString(UserEntity.COL_MARITAL));
                entity.setNation(resultSet.getString(UserEntity.COL_NATION));
                entity.setPayNoPwd(resultSet.getBoolean(UserEntity.COL_PAY_NO_PWD));
                entity.setPayPwd(resultSet.getString(UserEntity.COL_PAY_PWD));
                entity.setPayNoPwdPoint(resultSet.getInt(UserEntity.COL_PAY_NO_PWD_POINT));
                entity.setAvatarUrl(resultSet.getString(UserEntity.COL_AVATAR_URL));
                entity.setPost(resultSet.getString(UserEntity.COL_POST));
                entity.setJobNo(resultSet.getString(UserEntity.COL_JOB_NO));
                entity.setExtUserId(resultSet.getString(UserEntity.COL_EXT_USER_ID));
                entity.setAuthenticateDate(resultSet.getTimestamp(UserEntity.COL_AUTHENTICATE_DATE));
                entity.setLatestLoginTime(resultSet.getTimestamp(UserEntity.COL_LATEST_LOGIN_TIME));
                entity.setLatestChangePwdTime(resultSet.getTimestamp(UserEntity.COL_LATEST_CHANGE_PWD_TIME));
                entity.setDescription(resultSet.getString(UserEntity.COL_DESCRIPTION));
                entity.setDeptNo(resultSet.getString(UserEntity.COL_DEPT_NO));
                entity.setShareCode(resultSet.getString(UserEntity.COL_SHARE_CODE));
                entity.setParentShareCode(resultSet.getString(UserEntity.COL_PARENT_SHARE_CODE));
                entity.setIntegral(resultSet.getLong(UserEntity.COL_INTEGRAL));
                entity.setProvince(resultSet.getString(UserEntity.COL_PROVINCE));
                entity.setCity(resultSet.getString(UserEntity.COL_CITY));
                entity.setDistrict(resultSet.getString(UserEntity.COL_DISTRICT));
                entity.setProfession(resultSet.getString(UserEntity.COL_PROFESSION));
                entity.setConstellation(resultSet.getString(UserEntity.COL_CONSTELLATION));
                entity.setThirdPartyUserName(resultSet.getString(UserEntity.COL_THIRD_PARTY_USER_NAME));
                String partyPwd = resultSet.getString(UserEntity.COL_THIRD_PARTY_PWD);
                try {
                    boolean encryptPwdEnabled = ConfigHelper.getBoolean(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_ENABLE_KEY, false);
                    String encryptPwd = ConfigHelper.getString(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_KEY, "szyh$123");
                    if (encryptPwdEnabled && StringUtils.isNotEmpty(partyPwd)) {
                        byte[] decrypt = CryptUtil.decrypt(CryptUtil.decodeBase64(partyPwd), encryptPwd);
                        entity.setThirdPartyPwd(new String(decrypt));
                    } else {
                        entity.setThirdPartyPwd(partyPwd);
                    }
                } catch (Exception e) {
                    logger.error("第三方密码解密失败：", e);
                }

                //处理日志字段
                retrieveAuditField(entity, resultSet);

                return entity;
            }
        };
    }
}
