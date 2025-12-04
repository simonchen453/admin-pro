package com.adminpro.core.tools.seq;

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.sqlbuilder.DeleteBuilder;
import com.adminpro.core.jdbc.sqlbuilder.InsertBuilder;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import com.adminpro.core.jdbc.sqlbuilder.UpdateBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * 公共序列表 数据库持久层
 *
 * @author simon
 * @date 2018-09-06
 */
@Component
public class SequenceDao extends BaseDao<SequenceEntity, String> {

    public QueryResultSet<SequenceEntity> search(SearchParam param) {
        SelectBuilder<SequenceEntity> select = new SelectBuilder<SequenceEntity>(getCommonsSequenceRowMapper());
        select.setTable(SequenceEntity.TABLE_NAME);
        select.setSearchParam(param);
        return search(select);
    }

    /**
     * 创建 SequenceEntity
     *
     * @param entity
     */
    @Override
    public void create(SequenceEntity entity) {
        InsertBuilder insert = new InsertBuilder(SequenceEntity.TABLE_NAME);
        handleAuditColumnValues(insert, entity);
        insert.addColumnValue(SequenceEntity.COL_SEQ_NAME, entity.getSeqName());
        insert.addColumnValue(SequenceEntity.COL_SEQ_NEXT_VALUE, entity.getSeqNextValue());
        execute(insert);
    }

    /**
     * 更新 SequenceEntity
     *
     * @param entity
     */
    @Override
    public void update(SequenceEntity entity) {
        UpdateBuilder update = new UpdateBuilder(SequenceEntity.TABLE_NAME);
        handleAuditColumnValues(update, entity);
        update.addColumnValue(SequenceEntity.COL_SEQ_NAME, entity.getSeqName());
        update.addColumnValue(SequenceEntity.COL_SEQ_NEXT_VALUE, entity.getSeqNextValue());
        update.addWhereAnd(SequenceEntity.COL_SEQ_NAME + " = ?", entity.getSeqName());
        execute(update);
    }

    /**
     * 根据seqName查找SequenceEntity对象
     *
     * @param seqName
     * @return
     */
    @Override
    public SequenceEntity findById(String seqName) {
        SelectBuilder<SequenceEntity> select = new SelectBuilder<>(getCommonsSequenceRowMapper());
        select.setTable(SequenceEntity.TABLE_NAME);
        select.addWhereAnd(SequenceEntity.COL_SEQ_NAME + " = ? ", seqName);
        return executeSingle(select);
    }

    /**
     * 删除SequenceEntity
     *
     * @param seqName
     * @return
     */
    @Override
    public void delete(String seqName) {
        DeleteBuilder delete = new DeleteBuilder(SequenceEntity.TABLE_NAME);
        delete.addWhereAnd(SequenceEntity.COL_SEQ_NAME + " = ? ", seqName);
        execute(delete);
    }

    public long getNextLong(String seqName) {
        Long seq = execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call JDBC_SP_COMMON_SEQUENCE (?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, seqName);// 设置输入参数的值
                cs.registerOutParameter(2, Types.BIGINT);// 注册输出参数的类型
                return cs;
            }
        }, new CallableStatementCallback<Long>() {
            @Override
            public Long doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                cs.execute();
                return cs.getLong(2);// 获取输出参数的值
            }
        });
        return seq;
    }

    /**
     * SequenceEntity表映射关系
     *
     * @return
     */
    protected RowMapper<SequenceEntity> getCommonsSequenceRowMapper() {
        return new RowMapper<SequenceEntity>() {
            @Override
            public SequenceEntity mapRow(ResultSet resultSet, int i) throws SQLException {
                SequenceEntity entity = new SequenceEntity();

                entity.setSeqName(resultSet.getString(SequenceEntity.COL_SEQ_NAME));
                entity.setSeqNextValue(resultSet.getLong(SequenceEntity.COL_SEQ_NEXT_VALUE));

                //处理日志字段
                retrieveAuditField(entity, resultSet);

                return entity;
            }
        };
    }
}
