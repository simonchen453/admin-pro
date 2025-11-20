package com.adminpro.tools.domains.entity.oss;

import com.adminpro.core.base.entity.BaseAuditEntity;
import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.core.jdbc.sqlbuilder.UpdateBuilder;
import com.adminpro.rbac.domains.vo.oss.ListOssDto;
import com.adminpro.tools.domains.enums.OSSStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 文件上传表 数据库持久层
 *
 * @author simon
 * @date 2018-09-06
 */
@Component
public class OSSDao extends BaseDao<OSSEntity, String> {

    public QueryResultSet<ListOssDto> search(SearchParam param) {
        String sql = "select o.*, u.col_display, u.col_login_name " +
                "from sys_oss_tbl o left join sys_user_tbl u on " +
                "o.col_created_by_user_id=u.col_user_id and o.col_created_by_user_domain=u.col_user_domain";
        SelectBuilder<ListOssDto> select = new SelectBuilder<ListOssDto>(new RowMapper<ListOssDto>() {
            @Override
            public ListOssDto mapRow(ResultSet resultSet, int i) throws SQLException {
                ListOssDto dto = new ListOssDto();
                dto.setId(resultSet.getString(OSSEntity.COL_ID));
                dto.setUrl(resultSet.getString(OSSEntity.COL_URL));
                dto.setKey(resultSet.getString(OSSEntity.COL_KEY));
                dto.setHash(resultSet.getString(OSSEntity.COL_HASH));
                dto.setOriginal(resultSet.getString(OSSEntity.COL_ORIGINAL));
                dto.setStatus(resultSet.getString(OSSEntity.COL_STATUS));
                dto.setType(resultSet.getString(OSSEntity.COL_TYPE));
                dto.setSize(resultSet.getLong(OSSEntity.COL_SIZE));
                dto.setCreatedDate(resultSet.getTimestamp(BaseAuditEntity.COL_CREATED_DATE));
                dto.setCreatedBy(resultSet.getString("col_display"));
                return dto;
            }
        });
        select.setQuery(sql);
        select.setSearchParam(param);
        select.addOrderByDescending(OSSEntity.COL_CREATED_DATE);
        return search(select);
    }

    public List<OSSEntity> findByBatchId(String batchId) {
        SelectBuilder<OSSEntity> select = new SelectBuilder<OSSEntity>(OSSEntity.class);
        select.addWhereAnd(OSSEntity.COL_BATCH_ID + " = ? ", batchId);
        select.addOrderByAscending(OSSEntity.COL_CREATED_DATE);
        return execute(select);
    }

    public int active(String batchId) {
        UpdateBuilder builder = new UpdateBuilder(OSSEntity.TABLE_NAME);
        builder.addColumnValue(OSSEntity.COL_STATUS, OSSStatus.ACTIVE.getCode());
        builder.addWhereAnd(OSSEntity.COL_BATCH_ID + "=?", batchId);
        return execute(builder);
    }

    public void deleteByBatchId(String batchId) {
        DeleteBuilder delete = new DeleteBuilder(OSSEntity.TABLE_NAME);
        delete.addWhereAnd(OSSEntity.COL_BATCH_ID + " = ? ", batchId);
        execute(delete);
    }
}
