package com.adminpro.system.tools.domains.entity.exceptionlog;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 异常 服务层实现
 *
 * @author simon
 * @date 2018-11-29
 */
@Service
public class ExceptionLogService extends BaseService<ExceptionLogEntity, String> {

    private final ExceptionLogDao dao;

    public ExceptionLogService(ExceptionLogDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static ExceptionLogService getInstance() {
        return SpringUtil.getBean(ExceptionLogService.class);
    }

    public QueryResultSet<ExceptionLogEntity> search(SearchParam param) {
        return dao.search(param);
    }

    /**
     * 创建 ExceptionLogEntity
     *
     * @param entity
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(ExceptionLogEntity entity) {
        entity.setId(IdGenerator.getInstance().nextStringId());
        super.create(entity);
    }

    @Transactional
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }
        String[] split = ids.split(",");
        for (int i = 0; i < split.length; i++) {
            dao.delete(split[i]);
        }
    }
}
