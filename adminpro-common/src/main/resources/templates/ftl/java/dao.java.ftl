package ${package};

import com.adminpro.core.base.entity.BaseDao;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.sqlbuilder.SelectBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;

/**
 * ${tableComment}表 数据库持久层
 *
 * @author ${author}
 * @date ${datetime}
 */
@Slf4j
@Repository
public class ${className}Dao extends BaseDao<${className}Entity, ${primaryKey.attrType}> {

    /**
     * 根据查询参数获取分页的记录
     *
     * @param param
     * @return
     */
    public QueryResultSet<${className}Entity> search(SearchParam param){
        SelectBuilder<${className}Entity> select = new SelectBuilder<>(${className}Entity.class);
        select.setSearchParam(param);
        prepareSelectBuilder(select, param);
        return search(select);
    }

    /**
     * 根据查询参数获取所有的记录
     *
     * @param param
     * @return
     */
    public List<${className}Entity> findByParam(SearchParam param){
        SelectBuilder<${className}Entity> select = new SelectBuilder<>(${className}Entity.class);
        prepareSelectBuilder(select, param);
        return execute(select);
    }

    /**
     * 准备查询条件
     *
     * @param param
     * @return
     */
    private void prepareSelectBuilder(SelectBuilder select, SearchParam param){
        //TODO 页面过滤条件
        Map<String, Object> filters = param.getFilters();
        String condition = (String)filters.get("condition");
        if(StringUtils.isNotEmpty(condition)){
            //select.addWhereAnd(${className}Entity.COL_TITLE + " like ?", "%" + condition+"%");
        }
    }

    /**
     * ${className}Entity表映射关系
     * @return
     */
    protected RowMapper<${className}Entity> get${className}RowMapper(){
        return new RowMapper<${className}Entity>() {
            @Override
            public ${className}Entity mapRow(ResultSet resultSet, int i) throws SQLException {
                ${className}Entity entity = new ${className}Entity();

                <#list entityColumns as column>
                    entity.set${column.attrName}(resultSet.get${column.attrtype}(${className}Entity.${column.columnNameUp}));
                </#list>

                //处理日志字段
                retrieveAuditField(entity, resultSet);

                return entity;
            }
        };
    }
}

