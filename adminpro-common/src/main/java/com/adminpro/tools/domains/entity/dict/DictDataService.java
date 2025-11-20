package com.adminpro.tools.domains.entity.dict;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典数据 服务层实现
 *
 * @author simon
 * @date 2020-05-21
 */
@Service
public class DictDataService extends BaseService<DictDataEntity, String> {

    private DictDataDao dao;

    @Autowired
    public DictDataService(DictDataDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static DictDataService getInstance() {
        return SpringUtil.getBean(DictDataService.class);
    }

    public QueryResultSet<DictDataEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<DictDataEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
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
