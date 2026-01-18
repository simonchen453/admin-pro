package com.adminpro.system.rbac.domains.entity.device;

import com.adminpro.framework.base.entity.BaseDao;
import com.adminpro.framework.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.framework.jdbc.sqlbuilder.InsertBuilder;
import com.adminpro.framework.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.framework.jdbc.sqlbuilder.UpdateBuilder;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 用户设备 DAO
 *
 * @author adminpro
 * @since 1.0.0
 */
@Component
public class UserDeviceDao extends BaseDao<UserDeviceEntity, String> {

    /**
     * 根据设备ID和用户ID查找
     */
    public UserDeviceEntity findByDeviceId(String userId, String deviceId) {
        SelectBuilder<UserDeviceEntity> select = new SelectBuilder<>(getRowMapper());
        select.setTable(UserDeviceEntity.TABLE_NAME);
        select.addWhereAnd(UserDeviceEntity.COL_USER_ID + EQ, userId);
        select.addWhereAnd(UserDeviceEntity.COL_DEVICE_ID + EQ, deviceId);
        return executeSingle(select);
    }

    /**
     * 查找用户的所有设备
     */
    public List<UserDeviceEntity> findByUserId(String userId) {
        SelectBuilder<UserDeviceEntity> select = new SelectBuilder<>(getRowMapper());
        select.setTable(UserDeviceEntity.TABLE_NAME);
        select.addWhereAnd(UserDeviceEntity.COL_USER_ID + EQ, userId);
        return execute(select);
    }

    /**
     * 根据 Refresh Token JTI 查找
     */
    public UserDeviceEntity findByRefreshTokenJti(String jti) {
        SelectBuilder<UserDeviceEntity> select = new SelectBuilder<>(getRowMapper());
        select.setTable(UserDeviceEntity.TABLE_NAME);
        select.addWhereAnd(UserDeviceEntity.COL_REFRESH_TOKEN_JTI + EQ, jti);
        return executeSingle(select);
    }

    /**
     * 删除指定设备
     */
    public void deleteByDeviceId(String userId, String deviceId) {
        DeleteBuilder delete = new DeleteBuilder(UserDeviceEntity.TABLE_NAME);
        delete.addWhereAnd(UserDeviceEntity.COL_USER_ID + EQ, userId);
        delete.addWhereAnd(UserDeviceEntity.COL_DEVICE_ID + EQ, deviceId);
        execute(delete);
    }

    /**
     * 删除用户所有设备
     */
    public void deleteByUserId(String userId) {
        DeleteBuilder delete = new DeleteBuilder(UserDeviceEntity.TABLE_NAME);
        delete.addWhereAnd(UserDeviceEntity.COL_USER_ID + EQ, userId);
        execute(delete);
    }

    @Override
    public UserDeviceEntity findById(String id) {
        SelectBuilder<UserDeviceEntity> select = new SelectBuilder<>(getRowMapper());
        select.setTable(UserDeviceEntity.TABLE_NAME);
        select.addWhereAnd(UserDeviceEntity.COL_ID + EQ, id);
        return executeSingle(select);
    }

    @Override
    public void delete(String id) {
        DeleteBuilder delete = new DeleteBuilder(UserDeviceEntity.TABLE_NAME);
        delete.addWhereAnd(UserDeviceEntity.COL_ID + EQ, id);
        execute(delete);
    }

    public void update(UserDeviceEntity entity) {
        UpdateBuilder update = new UpdateBuilder(UserDeviceEntity.TABLE_NAME);
        update.addColumnValue(UserDeviceEntity.COL_USER_ID, entity.getUserId());
        update.addColumnValue(UserDeviceEntity.COL_DEVICE_ID, entity.getDeviceId());
        update.addColumnValue(UserDeviceEntity.COL_PLATFORM, entity.getPlatform());
        update.addColumnValue(UserDeviceEntity.COL_DEVICE_NAME, entity.getDeviceName());
        update.addColumnValue(UserDeviceEntity.COL_REFRESH_TOKEN_JTI, entity.getRefreshTokenJti());
        update.addColumnValue(UserDeviceEntity.COL_LAST_IP, entity.getLastIp());
        update.addColumnValue(UserDeviceEntity.COL_LAST_USER_AGENT, entity.getLastUserAgent());
        update.addColumnValue(UserDeviceEntity.COL_LAST_ACTIVE_AT, entity.getLastActiveAt());
        update.addColumnValue(UserDeviceEntity.COL_IS_ACTIVE, entity.getIsActive());
        update.addColumnValue(UserDeviceEntity.COL_UPDATED_AT, entity.getUpdatedAt());
        update.addWhereAnd(UserDeviceEntity.COL_ID + EQ, entity.getId());
        execute(update);
    }

    public void insert(UserDeviceEntity entity) {
        InsertBuilder insert = new InsertBuilder(UserDeviceEntity.TABLE_NAME);
        insert.addColumnValue(UserDeviceEntity.COL_ID, entity.getId());
        insert.addColumnValue(UserDeviceEntity.COL_USER_ID, entity.getUserId());
        insert.addColumnValue(UserDeviceEntity.COL_DEVICE_ID, entity.getDeviceId());
        insert.addColumnValue(UserDeviceEntity.COL_PLATFORM, entity.getPlatform());
        insert.addColumnValue(UserDeviceEntity.COL_DEVICE_NAME, entity.getDeviceName());
        insert.addColumnValue(UserDeviceEntity.COL_REFRESH_TOKEN_JTI, entity.getRefreshTokenJti());
        insert.addColumnValue(UserDeviceEntity.COL_LAST_IP, entity.getLastIp());
        insert.addColumnValue(UserDeviceEntity.COL_LAST_USER_AGENT, entity.getLastUserAgent());
        insert.addColumnValue(UserDeviceEntity.COL_LAST_ACTIVE_AT, entity.getLastActiveAt());
        insert.addColumnValue(UserDeviceEntity.COL_IS_ACTIVE, entity.getIsActive());
        execute(insert);
    }

    /**
     * 统计活跃设备数
     */
    public long countActiveDevices() {
        SelectBuilder<UserDeviceEntity> select = new SelectBuilder<>(getRowMapper());
        select.setTable(UserDeviceEntity.TABLE_NAME);
        select.addWhereAnd(UserDeviceEntity.COL_IS_ACTIVE + EQ, 1);
        return count(select);
    }

    protected RowMapper<UserDeviceEntity> getRowMapper() {
        return new RowMapper<UserDeviceEntity>() {
            @Override
            public UserDeviceEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                UserDeviceEntity entity = new UserDeviceEntity();
                entity.setId(rs.getString(UserDeviceEntity.COL_ID));
                entity.setUserId(rs.getString(UserDeviceEntity.COL_USER_ID));
                entity.setDeviceId(rs.getString(UserDeviceEntity.COL_DEVICE_ID));
                entity.setPlatform(rs.getString(UserDeviceEntity.COL_PLATFORM));
                entity.setDeviceName(rs.getString(UserDeviceEntity.COL_DEVICE_NAME));
                entity.setRefreshTokenJti(rs.getString(UserDeviceEntity.COL_REFRESH_TOKEN_JTI));
                entity.setLastIp(rs.getString(UserDeviceEntity.COL_LAST_IP));
                entity.setLastUserAgent(rs.getString(UserDeviceEntity.COL_LAST_USER_AGENT));

                java.sql.Timestamp lastActive = rs.getTimestamp(UserDeviceEntity.COL_LAST_ACTIVE_AT);
                if (lastActive != null)
                    entity.setLastActiveAt(lastActive.toLocalDateTime());

                entity.setIsActive(rs.getInt(UserDeviceEntity.COL_IS_ACTIVE));

                // BaseAuditEntity
                java.sql.Timestamp createdAt = rs.getTimestamp(UserDeviceEntity.COL_CREATED_AT);
                if (createdAt != null)
                    entity.setCreatedAt(createdAt);

                java.sql.Timestamp updatedAt = rs.getTimestamp(UserDeviceEntity.COL_UPDATED_AT);
                if (updatedAt != null)
                    entity.setUpdatedAt(updatedAt);

                return entity;
            }
        };
    }
}
