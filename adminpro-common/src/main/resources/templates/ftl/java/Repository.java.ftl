package ${package};

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * ${tableComment}表 数据库持久层
 *
 * @author ${author}
 * @date ${datetime}
 */
@Repository
public interface ${className}Repo extends JpaRepository<${className}Entity, ${primaryKey.attrType}>, JpaSpecificationExecutor<${className}Entity> {
}

