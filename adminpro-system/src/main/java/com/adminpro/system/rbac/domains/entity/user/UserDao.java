package com.adminpro.system.rbac.domains.entity.user;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.base.util.CryptUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.framework.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.system.core.common.helper.ConfigHelper;
import com.adminpro.system.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.system.web.BaseConstants;
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
public class UserDao extends BaseDao<UserEntity, String> {

    private static final String SQL_USER_LIST = "select u.* from sys_user_tbl u left join sys_dept_tbl d on u.col_dept_no = d.col_no";
    private static final String IN = " IN ";

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

    private void prepareSelectBuilder(SelectBuilder<UserEntity> select, SearchParam param) {
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
            select.addWhereAnd("(d." + DeptEntity.COL_ID
                    + " = ? or d.col_id in (select t.col_id from sys_dept_tbl t where find_in_set (?, col_ancestors)))",
                    deptId, deptId);
        }
    }

    /**
     * 根据id查找UserEntity对象
     *
     * @param id
     * @return
     */
    @Override
    public UserEntity findById(String id) {
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_ID + EQ, id);
        return executeSingle(select);
    }

    /**
     * 根据ID列表批量查询用户
     *
     * @param ids 用户ID列表
     * @return 用户列表
     */
    public List<UserEntity> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        SelectBuilder<UserEntity> select = new SelectBuilder<UserEntity>(getUserRowMapper());
        select.setTable(UserEntity.TABLE_NAME);
        select.addWhereAnd(UserEntity.COL_ID + IN, ids.toArray());
        return execute(select);
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

    /**
     * 删除UserEntity
     *
     * @param userDomain
     * @param loginName
     * @return
     */
    public void delete(String userDomain, String loginName) {
        DeleteBuilder delete = new DeleteBuilder(UserEntity.TABLE_NAME);
        delete.addWhereAnd(UserEntity.COL_USER_DOMAIN + EQ, userDomain);
        delete.addWhereAnd(UserEntity.COL_LOGIN_NAME + EQ, loginName);
        execute(delete);
    }

    @Override
    public void delete(String id) {
        DeleteBuilder delete = new DeleteBuilder(UserEntity.TABLE_NAME);
        delete.addWhereAnd(UserEntity.COL_ID + EQ, id);
        execute(delete);
    }

    /**
     * 批量删除UserEntity（优化性能）
     *
     * @param ids 用户ID列表
     */
    public void deleteByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        DeleteBuilder delete = new DeleteBuilder(UserEntity.TABLE_NAME);
        delete.addWhereAnd(UserEntity.COL_ID + IN, ids.toArray());
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

                String id = resultSet.getString(UserEntity.COL_ID);
                String userDomain = resultSet.getString(UserEntity.COL_USER_DOMAIN);
                entity.setId(id);
                entity.setUserDomain(userDomain);
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
                    boolean encryptPwdEnabled = ConfigHelper
                            .getBoolean(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_ENABLE_KEY, false);
                    String encryptPwd = ConfigHelper.getString(BaseConstants.THIRD_PARTY_ENCRYPT_PWD_KEY);
                    if (encryptPwdEnabled && StringUtils.isNotEmpty(partyPwd)) {
                        byte[] decrypt = CryptUtil.decrypt(CryptUtil.decodeBase64(partyPwd), encryptPwd);
                        entity.setThirdPartyPwd(new String(decrypt));
                    } else {
                        entity.setThirdPartyPwd(partyPwd);
                    }
                } catch (Exception e) {
                    logger.error("第三方密码解密失败：", e);
                }

                // 处理日志字段
                retrieveAuditField(entity, resultSet);

                return entity;
            }
        };
    }
}
