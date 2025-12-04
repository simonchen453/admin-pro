package ${package};

import com.adminpro.framework.common.entity.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import jakarta.persistence.*;
<#list entityColumns as column>
<#if column.attrType == 'Date'>
import java.util.Date;
    <#break>
</#if>
</#list>

/**
 * ${tableComment}表 ${tableName}
 *
 * @author ${author}
 * @date ${datetime}
 */
@Entity
@Table(name = ${className}Entity.TABLE_NAME)
public class ${className}Entity extends ${parentEntity} {
    private static final long serialVersionUID = 1L;
    /**
    * 表名
    */
    public static final String TABLE_NAME = "${TABLENAME}";

<#list entityColumns as column>
    /**
     * ${column.columnComment}
     */
    public static final String ${column.columnNameUp} = "${column.columnNameUp}";
</#list>

<#list entityColumns as column>
    /**
     * ${column.columnComment}
     */
    private ${column.attrType} ${column.attrname};
</#list>

<#list entityColumns as column>
    public void set${column.attrName}(${column.attrType} ${column.attrname}) {
        this.${column.attrname} = ${column.attrname};
    }

    <#if column.attrname == primaryKey.attrname>
    @Id
    @TableGenerator(pkColumnValue = "${TABLENAME}_ID", name = BaseEntity.PK_GEN_NAME, table = BaseEntity.PK_GEN_TABLE, pkColumnName = BaseEntity.PK_GEN_COLUMN,
            valueColumnName = BaseEntity.PK_GEN_VALUE, allocationSize = BaseEntity.PK_GEN_ALLOCATION_SIZE)
    @GeneratedValue(generator = BaseEntity.PK_GEN_NAME, strategy = GenerationType.TABLE)
    </#if>
    @Column(name = ${column.columnNameUp})
    public ${column.attrType} get${column.attrName}() {
        return ${column.attrname};
    }

</#list>

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        <#list columns as column>
        .append("${column.attrname}" , get${column.attrName}())
        </#list>
        .toString();
    }
}

